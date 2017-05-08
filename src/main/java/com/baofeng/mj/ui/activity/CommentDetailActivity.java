package com.baofeng.mj.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.GameApi;
import com.baofeng.mj.util.viewutil.LanguageValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hanyang on 2016/5/10.
 * 评论界面
 */
public class CommentDetailActivity extends BaseActivity implements View.OnClickListener {
    private EditText comment_edit;
    private TextView comment_len, comment_tag;
    private ImageButton comment_detail_back;
    private TextView comment_title, send_comment;
    private RatingBar comment_detail_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        initView();
    }

    private void initView() {
        comment_edit = (EditText) findViewById(R.id.comment_edit);
        comment_len = (TextView) findViewById(R.id.comment_len);
        comment_detail_back = (ImageButton) findViewById(R.id.comment_detail_back);
        comment_title = (TextView) findViewById(R.id.comment_title);
        comment_title.setText(getIntent().getStringExtra("title"));
        send_comment = (TextView) findViewById(R.id.send_comment);
        send_comment.setText(LanguageValue.getInstance().getValue(this, "SID_SEND"));
        comment_detail_score = (RatingBar) findViewById(R.id.comment_detail_score);
        comment_detail_back.setOnClickListener(this);
        send_comment.setOnClickListener(this);
        showKeyBoard(comment_edit);
        addEditListener(comment_edit);
        comment_tag = (TextView) findViewById(R.id.comment_tag);
        comment_tag.setText(LanguageValue.getInstance().getValue(this, "SID_CLICK_TO_SCORE"));
    }

    private void hiddenKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void showKeyBoard(final EditText editText) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editText, 0);
                           }
                       },
                998);
    }

    private void addEditListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int len = s.toString().length();
                if (len <= 120) {
                    comment_len.setText(len + "/120");
                } else {
                    comment_len.setText("120/120");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.comment_detail_back == id) {
            hiddenKeyBoard(comment_edit);
            finish();
        } else if (R.id.send_comment == id) {
            if (UserSpBusiness.getInstance().isUserLogin()) {
                sendComment();
            } else {
                Intent intent = new Intent(CommentDetailActivity.this, LoginActivity.class);
                if (getIntent() != null) {
                    intent.putExtra("id", getIntent().getStringExtra("id"));
                    intent.putExtra("title", getIntent().getStringExtra("title"));
                    intent.putExtra("score", Math.round(comment_detail_score.getRating()));
                    intent.putExtra("content", comment_edit.getText()
                            .toString().trim());
                }
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * 发布评论
     */

    private void sendComment() {
        if (checkComment()) {
            String uid = UserSpBusiness.getInstance().getUid();
            String res_id = getIntent().getStringExtra("id");
            int res_type = 100;
            String res_name = getIntent().getStringExtra("title");
            int score = Math.round(comment_detail_score.getRating());
            String content = comment_edit.getText()
                    .toString().trim();
            String nickName = UserSpBusiness.getInstance().getNickName();
            if (nickName == null || "".equals(nickName)) {
                nickName = "0";
            }
            new GameApi().sendComment(this, uid, nickName, res_id, res_type, res_name, score, content, new ApiCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    super.onSuccess(result);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("status") == 1) {
                            Toast.makeText(CommentDetailActivity.this, "评论成功，请等待审核通过！", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(CommentDetailActivity.this, "评论失败，请再试一次！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                    Toast.makeText(CommentDetailActivity.this, "评论失败，请再试一次！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 检测评论内容和评分是否符合规范
     *
     * @return
     */
    private boolean checkComment() {
        String content = comment_edit.getText()
                .toString().trim();
        String score = String.valueOf(comment_detail_score.getRating());
        if (content.length() < 5) {
            Toast.makeText(this, "评论字数不能少于五个", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (score.equals("0.0")) {
            Toast.makeText(this, "请输入游戏评分", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
