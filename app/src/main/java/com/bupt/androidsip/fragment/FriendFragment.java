package com.bupt.androidsip.fragment;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.androidsip.R;
import com.bupt.androidsip.activity.BaseActivity;
import com.bupt.androidsip.activity.DemoWifiChatActivity;
import com.bupt.androidsip.customview.SlideBar;
import com.bupt.androidsip.entity.Friend;
import com.bupt.androidsip.mananger.UserManager;
import com.bupt.androidsip.mananger.WifiDirectManager;
import com.wang.avi.AVLoadingIndicatorView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WHY on 2017/7/3.
 */

public class FriendFragment extends BaseFragment {

    WifiDirectManager wifiDirectManager;


    EditText searchText;

    TextView searchSubmit;

    GridView gridView;

    @BindView(R.id.frag_friend_append)
    TextView append;
    @BindView(R.id.friend_list)
    ListView listView;
    @BindView(R.id.frag_friend_slidebar)
    SlideBar slideBar;

    HotFriendGridAdapter gridAdapter;
    SortAdapter sortAdapter;

    Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_friend, null);
        View headerView = inflater.inflate(R.layout.header_frag_friend, null);

        showFragLoadingView(v);
        handler = new Handler();
        handler.postDelayed(() -> hideFragLoadingView(v), 2000);

        ButterKnife.bind(this, v);
        wifiDirectManager = WifiDirectManager.getInstance(getActivity());
        append.setOnClickListener(e -> popWifiDialog());

        //listView.addHeaderView(headerView);
        sortAdapter = new SortAdapter(getActivity(), UserManager.getInstance().getUser().friends, (o1, o2) ->
                o1.getSortLetters().compareTo(o2.getSortLetters()));
        slideBar.setLetterList(getLetterList(UserManager.getInstance().getUser().friends));
        //slideBar.setChoose('好');
        slideBar.setOnTouchingLetterChangedListener(s -> {
            if (s.equals("好")) {
                listView.setSelection(0);
            } else {
                //cityListView.smoothScrollToPosition(sortAdapter.getPositionOfSection(s.charAt(0)));
                listView.setSelection(sortAdapter.getPositionOfSection(s.charAt(0)) + listView.getHeaderViewsCount());
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (sortAdapter == null)
                    return;
                int section = sortAdapter.getSectionForPosition(firstVisibleItem);
                slideBar.setChoose((char) section);
            }
        });
        listView.setAdapter(sortAdapter);
        return v;
    }

    private void initListHeader(View header) {
        searchText = (EditText) header.findViewById(R.id.frag_friend_search_edit);
        searchSubmit = (TextView) header.findViewById(R.id.frag_friend_search_confirm);
        append = (TextView) header.findViewById(R.id.frag_friend_append);
        gridView = (GridView) header.findViewById(R.id.frag_friend_hot_friend);
        //append.setOnClickListener(v -> popWifiDialog());

    }

    static class HotFriendGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

    static class SortAdapter extends BaseAdapter implements SectionIndexer {

        private Comparator<Friend> comparator;
        private List<Friend> list = null;
        private Context mContext;

        public SortAdapter(Context mContext, List<Friend> list, Comparator<Friend> comparator) {
            this.mContext = mContext;
            this.list = list;
            this.comparator = comparator;
            Collections.sort(list, comparator);
        }

        /**
         * 当ListView数据发生变化时,调用此方法来更新ListView
         *
         * @param list
         */
        public void updateListView(List<Friend> list) {
            this.list = list;
            Collections.sort(this.list, comparator);
            notifyDataSetChanged();
        }

        public int getCount() {
            return this.list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View view, ViewGroup arg2) {
            ViewHolder holder = null;
            final Friend friend = list.get(position);
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_list, null);
                holder.section = (TextView) view.findViewById(R.id.item_friend_list_section);
                holder.headImg = (ImageView) view.findViewById(R.id.item_friend_list_head);
                holder.maskView = view.findViewById(R.id.item_friend_list_mask);
                holder.nameTextView = (TextView) view.findViewById(R.id.item_friend_list_name);
                holder.desc = (TextView) view.findViewById(R.id.item_friend_list_desc);
                holder.stateTextView = (TextView) view.findViewById(R.id.item_friend_list_state);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            int section = getSectionForPosition(position);
            if (position == getPositionForSection(section)) {//如果是该类第一个
                holder.section.setVisibility(View.VISIBLE);
                holder.section.setText(friend.getSortLetters().substring(0, 1));
            } else {
                holder.section.setVisibility(View.GONE);
            }
            //屏蔽title的点击事件
            holder.section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.headImg.setImageDrawable((friend.head == 1) ? mContext.getResources().getDrawable(R.drawable.xusong) :
                    mContext.getResources().getDrawable(R.drawable.ic_perm_data_setting_30px));
            holder.maskView.setVisibility(friend.state == 0 ? View.VISIBLE : View.INVISIBLE);
            holder.stateTextView.setText(friend.state == 0 ? "[离线]" : "[在线]");
            holder.nameTextView.setText(friend.name);
            holder.desc.setText(friend.desc);
            return view;

        }


        final class ViewHolder {
            RelativeLayout itemContainer;

            TextView section;
            ImageView headImg;
            View maskView;
            TextView nameTextView;
            TextView stateTextView;
            TextView desc;
        }

        public int getSectionForPosition(int position) {
            return list.get(position).getSortLetters().toUpperCase().charAt(0);
        }

        public int getPositionForSection(int section) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = list.get(i).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
                if (firstChar > section) {
                    break;
                }
            }
            return -1;
        }

        public int getPositionNoBiggerThanSection(int section) {
            int location = 0;
            for (int i = 0; i < getCount(); i++) {
                String sortStr = list.get(i).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
                if (firstChar > section) {
                    break;
                }
                location++;
            }
            return location;
        }

        public int getPositionOfSection(int section) {
            int location = 0;
            for (int i = 0; i < getCount(); i++) {
                String sortStr = list.get(i).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
                if (firstChar > section) {
                    break;
                }
                location++;
            }
            return location;
        }


        @Override
        public Object[] getSections() {
            return null;
        }


    }

    public List<String> getLetterList(List<Friend> friends) {
        List<String> indexes = new ArrayList<>();
        indexes.add("好");
        for (Friend f : friends) {
            String letter = f.getSortLetters().substring(0, 1).toUpperCase();
            if (!indexes.contains(letter))
                indexes.add(letter);
        }
        return indexes;
    }

    public void popWifiDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        RelativeLayout root = (RelativeLayout) LayoutInflater.from(getActivity()).
                inflate(R.layout.dialog_select_wifi, null);
        dialog.setContentView(root);
        dialog.setCancelable(true);
        dialog.setOnDismissListener((e) -> {
            wifiDirectManager.release();//停止接收广播
        });

        Window dialogWindow = dialog.getWindow();
        SwipeRefreshLayout refresher = (SwipeRefreshLayout) root.findViewById(R.id.select_wifi_swipe);
        refresher.setOnRefreshListener(() -> {
            handler.postDelayed(() -> refresher.setRefreshing(false), 2000);
        });
        ListView wifiList = (ListView) root.findViewById(R.id.select_wifi_list);

        SelectWifiAdapter adapter = new SelectWifiAdapter(new ArrayList<>(), getActivity());
        wifiList.setAdapter(adapter);
        wifiList.setOnItemClickListener((parent, view, position, id) -> {
            connectTo((WifiP2pDevice) adapter.getItem(position));
        });

        View loadingContainer = root.findViewById(R.id.wifi_loading_container);
        TextView tv = (TextView) loadingContainer.findViewById(R.id.wifi_loading_title);
        TextView createWifi = (TextView) loadingContainer.findViewById(R.id.select_wifi_create);
        //createWifi.setOnClickListener(v -> wifiDirectManager.createLocalService());//允许本地成为主机
        //p2p不用这个
        tv.setText("WiFi初始化中...");
        AVLoadingIndicatorView loadingIndicatorView = (AVLoadingIndicatorView) loadingContainer.findViewById(R.id.select_wifi_loader);

        wifiDirectManager.setWifiListener(new WifiDirectManager.WifiListener() {
            @Override
            public void onSearchBegin() {
                loadingContainer.setVisibility(View.VISIBLE);
                loadingIndicatorView.show();
            }

            @Override
            public void onSuccessStart() {
                tv.setText("开始寻找设备...");
                Toast.makeText(getActivity(), "初始化成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailStart() {
                Toast.makeText(getActivity(), "初始化失败 ！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSearchEnd() {

            }

            @Override
            public void onFindDevices(WifiP2pDeviceList devices) {
                Toast.makeText(getActivity(), "找到了" + devices.getDeviceList().size() + "台设备", Toast.LENGTH_SHORT).show();
                if (devices.getDeviceList().size() > 0) {//否则继续显示加载动画
                    loadingContainer.setVisibility(View.INVISIBLE);
                    loadingIndicatorView.hide();
                    adapter.updateDataSet(devices);
                }

            }

            @Override
            public void onChatPrepared() {//代表着chatmanager已经初始化完成，可以聊天了,这个方法会在其它线程调用
                handler.post(() -> Toast.makeText(getActivity(), "成功连接，可以开始聊天了", Toast.LENGTH_SHORT).show());
                DemoWifiChatActivity.Start(getActivity());
            }
        });
        wifiDirectManager.prepare();//准备阶段
        //开始进行search
        wifiDirectManager.beginSearch();
        dialogWindow.setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void connectTo(WifiP2pDevice device) {
        wifiDirectManager.talkToDevice(device);
    }

}

class SelectWifiAdapter extends BaseAdapter {

    Context context;
    List<WifiP2pDevice> devices;

    public void updateDataSet(WifiP2pDeviceList wifiP2pDeviceList) {
        devices.clear();
        devices.addAll(wifiP2pDeviceList.getDeviceList());
        notifyDataSetChanged();
    }

    public SelectWifiAdapter(List<WifiP2pDevice> devices, Context context) {
        this.devices = devices;
        this.context = context;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_wifi, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.item_wifi_device_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(devices.get(position).deviceName);
        return convertView;
    }

    static class ViewHolder {
        TextView name;
    }
}

