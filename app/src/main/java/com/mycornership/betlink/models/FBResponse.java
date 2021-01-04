package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

public class FBResponse {

    @SerializedName("success")
    private boolean success;
    @SerializedName("id")
    private String id;
    @SerializedName("post_id")
    private String postId;
    @SerializedName("error")
    private FBModel fbeModel;

    public FBResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public FBModel getFbeModel() {
        return fbeModel;
    }

    public void setFbeModel(FBModel fbeModel) {
        this.fbeModel = fbeModel;
    }

    @Override
    public String toString() {
        return "FBResponce{" +
                "success=" + success +
                ", id='" + id + '\'' +
                ", postId='" + postId + '\'' +
                ", fbeModel=" + fbeModel +
                '}';
    }
}

