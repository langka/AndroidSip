package com.bupt.androidsip.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.mananger.DBManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xusong on 2017/7/11.
 * About:
 */

public class DatabaseTestActivity extends BaseActivity {

    @BindView(R.id.db_insert)
    View insert;
    @BindView(R.id.db_delete)
    View delete;
    @BindView(R.id.db_print)
    View print;
    @BindView(R.id.db_board)
    TextView board;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        ButterKnife.bind(this);
        DBManager dbManager = DBManager.getInstance(this);
        insert.setOnClickListener(v->dbManager.save(new SipMessage()));
        delete.setOnClickListener(v->dbManager.delete(5));
        print.setOnClickListener(v->{
            List<SipMessage> sipMessages = dbManager.loadAllMessages();
            String text = "";
            for(SipMessage sipMessage:sipMessages){
                text+=sipMessage.content;
            }
            board.setText(text);
        });

    }
}
