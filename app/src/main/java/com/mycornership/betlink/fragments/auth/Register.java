package com.mycornership.betlink.fragments.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.ComboAdapter;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.models.User;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends Fragment {
    TextInputEditText edt_user, edt_ph, edt_em, edt_pass;
    Spinner spn_code;
    MaterialButton btn_reg;
    ProgressBar bar;
    TextView pass_info;
    final String photo ="https://sportinglinkstore145905-prod.s3.amazonaws.com/public/user/profile-pix-";
    String email = "";

    public Register() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize components
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init(){
        edt_user = getView().findViewById(R.id.username);
        edt_ph = getView().findViewById(R.id.phone);
        edt_em = getView().findViewById(R.id.email);
        edt_pass = getView().findViewById(R.id.password);
        pass_info = getView().findViewById(R.id.pass_info);

        spn_code = getView().findViewById(R.id.country_code);
        spn_code.setAdapter(new ComboAdapter(getContext(), Arrays.asList(getResources().getStringArray(R.array.countries_code))));
        bar = getView().findViewById(R.id.loading_bar);
        bar.setVisibility(View.GONE);

        btn_reg = getView().findViewById(R.id.btn_submit);
        btn_reg.setOnClickListener(v ->{
            //collect data from the UI
            String nm = edt_user.getText().toString().trim();
            String ph = edt_ph.getText().toString().trim();
            email = edt_em.getText().toString().trim();
            String ps = edt_pass.getText().toString().trim();
            String code = spn_code.getSelectedItem().toString();
            if(isValid(nm, ph, ps, code)){
                //shows progress
                bar.setVisibility(View.VISIBLE);
                if(spn_code.getSelectedItemPosition() != 0)
                    register(nm, ph, email, ps, code);
                else
                    Toast.makeText(getContext(), "Enter Country Code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid(String name, String phone, String pass, String code) {
        if(TextUtils.isEmpty(name)){
            edt_user.setError("enter your username");
            return false;
        }

        if(TextUtils.isEmpty(phone)){
            edt_user.setError("enter your phone number");
            return false;
        }

        String patterns = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";
        Pattern pattern = Pattern.compile(patterns);
        Matcher matcher = pattern.matcher(pass);
        if(!matcher.matches()){
            edt_pass.setError("invalid password");
            pass_info.setText("password must contain at least 1 Capital later, 1 small later, 1 number and 8 characters in number.");
            return false;
        }

        if(TextUtils.isEmpty(code)){
            return false;
        }

        return true;
    }

    private void register(String name, String phone, String em, String pass, String code){
        String ph, country, cd;
        if(phone.startsWith("0"))
            phone = phone.substring(1, phone.length());
        country = code.substring(0, code.indexOf('+'));
        cd = code.substring(code.indexOf('+') - 1, code.length());
        ph = cd + phone;
        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), ph.trim()));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.preferredUsername(), name));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.address(), country));

        //register new User
        Amplify.Auth.signUp(name, pass,
                AuthSignUpOptions.builder().userAttributes(attributes).build(),
                result ->{
                    //add user to the server
                    User u = new User();
                    u.setUsername(name);
                    u.setPhone(ph);
                    u.setCountry(country);
                    u.setPhoto(photo + name + ".jpg");
                    u.setRole("USER");
                    u.setWallet("5");
                    u.setEmail(em);
                    //register the user on the database
                    createNewUser(u);
                },
                error ->{
                    getActivity().runOnUiThread( () ->{
                        //hides the progress
                        bar.setVisibility(View.GONE);
                        Log.e("register_error", error.getCause().getMessage() ,error);
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
                );
    }

    private void createNewUser(User user){
        Log.d("reg_", user.toString());

        //add the user to the database
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<DataResponse> userCall = api.addUser(user);
        userCall.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                //hides the progress
                bar.setVisibility(View.GONE);
                confirm(user.getUsername());

                Log.d("reg_", response.body().toString());
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                //hides the progress
                bar.setVisibility(View.GONE);
                Log.d("user_error", t.getMessage());

                confirm(user.getUsername());
            }
        });
    }

    private void confirm(String user){
       // Toast.makeText(getContext(), "registration successful", Toast.LENGTH_SHORT).show();
        Bundle b = new Bundle();
        b.putString("username", user);
        Navigation.findNavController(getView()).navigate(R.id.confirmAccount, b);
    }

}
