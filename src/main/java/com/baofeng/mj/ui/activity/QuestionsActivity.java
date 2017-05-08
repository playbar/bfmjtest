package com.baofeng.mj.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.util.publicutil.PixelsUtil;

import java.util.ArrayList;

/**
 * ClassName: QuestionsActivity <br/>
 * @author wanghongfang    
 * @date: 2015-3-11 下午3:48:24 <br/>  
 * description:常见问题页面
 */
public class QuestionsActivity extends BaseActivity implements OnClickListener{
    
    private ExpandableListView expandListView;
    private MyExpandableListAdapter adapter;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        backBtn = (ImageButton) findViewById(R.id.question_back);
        backBtn.setOnClickListener(this);
        expandListView = (ExpandableListView)this.findViewById(R.id.expandlist);
        ArrayList<String> groupList = new ArrayList<String>();
        ArrayList<String> childList = new ArrayList<String>();
        String[] group = getResources().getStringArray(R.array.quesions);
        String[] child = getResources().getStringArray(R.array.answer);
        for(int i=0;i<group.length;i++){
            groupList.add(group[i]);
        }
        for(int i=0;i<child.length;i++){
            childList.add(child[i]);
        }
        adapter = new MyExpandableListAdapter(groupList, childList);
        expandListView.setGroupIndicator(null);
        expandListView.setAdapter(adapter);
        expandListView.setHeaderDividersEnabled(true);
        expandListView.setFooterDividersEnabled(true);
        expandListView.setOnGroupClickListener(new OnGroupClickListener() {
            
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                int index = expandListView.getFirstVisiblePosition();
                View groupview = expandListView.getChildAt(0);
//                int top = : groupview.getX();
//                expandListView.setSelectionFromTop(index, -top);
                expandListView.setSelectedGroup(groupPosition);
                return false;
            }
        });
        expandListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            
            @Override
            public void onGroupExpand(int groupPosition) {
                int count = adapter.getGroupCount();
                for (int i = 0; i < count; i++) {
                    if (i != groupPosition) {
                        expandListView.collapseGroup(i);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.question_back){
                finish();
        }
    }
    
      class MyExpandableListAdapter extends BaseExpandableListAdapter {
        private ArrayList<String> groupList;
        private ArrayList<String> childList;

        MyExpandableListAdapter(ArrayList<String> groupList,ArrayList<String> childList) {
            this.groupList = groupList;
            this.childList = childList;
        }

        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition);
        }

        private int selectedGroupPosition = -1;
        private int selectedChildPosition = -1;

        public void setSelectedPosition(int selectedGroupPosition, int selectedChildPosition) {
            this.selectedGroupPosition = selectedGroupPosition;
            this.selectedChildPosition = selectedChildPosition;
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView textView = null;
            if (convertView == null) {
                textView = new TextView(QuestionsActivity.this);
                textView.setTextColor(getResources().getColor(R.color.prompt_color));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.btn_text_size));
                textView.setPadding(PixelsUtil.dip2px(10), PixelsUtil.dip2px(15), PixelsUtil.dip2px(10), PixelsUtil.dip2px(30));
                convertView = textView;
            } else {
                textView = (TextView) convertView;
            }

            textView.setText(getChild(groupPosition, childPosition).toString());
//            textView.setText(getChild(groupPosition, childPosition).toString());StringUtil.ToDBC()
            textView.setClickable(false);
            textView.setOnClickListener(null); 
            textView.setBackgroundColor(Color.WHITE);
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        public int getGroupCount() {
            return groupList.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.expandlist_group_item, null);
            TextView textView = (TextView)view.findViewById(R.id.title_text);
            ImageView imgIndicator = (ImageView)view.findViewById(R.id.question_img);
            textView.setText(getGroup(groupPosition).toString());

            if (isExpanded) {
                imgIndicator.setImageResource(R.drawable.public_arrow_down);
                textView.setTextColor(getResources().getColor(R.color.content_color));
            } else {
                imgIndicator.setImageResource(R.drawable.public_arrow_right);
                textView.setTextColor(getResources().getColor(R.color.content_color));
            }
            return view;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
