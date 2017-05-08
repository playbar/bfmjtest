package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * Created by hanyang on 2016/6/15.
 * 资源单个用户评论信息
 */
public class CommentBean implements Serializable {
    private String nickname;
    private String score;
    private String content;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
