//package com.xie.framwork.adapter;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.chad.library.adapter.base.BaseViewHolder;
//import com.ebscn.callanswer.R;
//import com.ebscn.callanswer.bean.ConfigIconItem;
//import com.ebscn.callanswer.utils.GlideApp;
//import com.ebscn.callanswer.utils.GlideUtils;
//import com.ebscn.callanswer.utils.RetrofitManager;
//
//import java.util.Collections;
//import java.util.List;
//
//public class ServiceEditAdapter extends RecyclerView.Adapter<BaseViewHolder> {
//    public final static int TYPE_SERVICE = 1;
//    public final static int TYPE_TITLE = 2;
//
//    private List<ConfigIconItem> list;
//    private Context mContext;
//    private LayoutInflater mLiLayoutInflater;
//    private OnItemChildClickListener mOnItemChildClickListener;
//    private BaseQuickAdapter.SpanSizeLookup mSpanSizeLookup;
//    private int total_selected = 0;
//
//    public ServiceEditAdapter(List<ConfigIconItem> list, Context context) {
//        this.list = list;
//        this.mContext = context;
//        this.mLiLayoutInflater = LayoutInflater.from(mContext);
//        initTotalSelected();
//    }
//
//    private void initTotalSelected() {
//        if (null == list) {
//            total_selected = 0;
//            return;
//        }
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).getIconName().equals(mContext.getString(R.string.all_service))) {
//                total_selected = i;
//                break;
//            }
//        }
//    }
//
//    public void setData(List<ConfigIconItem> list) {
//        this.list = list;
//        initTotalSelected();
//        notifyDataSetChanged();
//    }
//
//    public List<ConfigIconItem> getList() {
//        return list;
//    }
//
//    public int getTotal_selected() {
//        return total_selected;
//    }
//
//    @Override
//    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == TYPE_SERVICE) {
//            return new BaseViewHolder(mLiLayoutInflater.inflate(R.layout.item_main_service, parent, false));
//        } else if (viewType == TYPE_TITLE) {
//            return new BaseViewHolder(mLiLayoutInflater.inflate(R.layout.item_service_edit_title, parent, false));
//        }
//        return null;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return list.get(position).getItemType();
//    }
//
//    @Override
//    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//        if (manager instanceof GridLayoutManager) {
//            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
//            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    return mSpanSizeLookup.getSpanSize(gridManager, position);
//                }
//            });
//        }
//    }
//
//    public void setSpanSizeLookup(BaseQuickAdapter.SpanSizeLookup mSpanSizeLookup) {
//        this.mSpanSizeLookup = mSpanSizeLookup;
//    }
//
//    @Override
//    public void onBindViewHolder(BaseViewHolder helper, int position) {
//        ConfigIconItem bean = list.get(position);
//        switch (helper.getItemViewType()) {
//            case TYPE_SERVICE:
//                helper.setText(R.id.name, bean.getIconName());
//                if (bean.isEdit() && bean.isSelect()) {
//                    helper.getView(R.id.edit_fl).setVisibility(View.VISIBLE);
//                    helper.setImageResource(R.id.edit, R.drawable.btn_sub_red);
//                    helper.getView(R.id.edit_fl).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (null != mOnItemChildClickListener) {
//                                mOnItemChildClickListener.onItemChildClick(v, position, true);
//                            }
//                        }
//                    });
//                } else if (bean.isEdit() && !bean.isSelect()) {
//                    helper.getView(R.id.edit_fl).setVisibility(View.VISIBLE);
//                    helper.setImageResource(R.id.edit, R.drawable.btn_add_blue);
//                    helper.getView(R.id.edit_fl).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (null != mOnItemChildClickListener) {
//                                mOnItemChildClickListener.onItemChildClick(v, position, false);
//                            }
//                        }
//                    });
//                } else {
//                    helper.getView(R.id.edit_fl).setVisibility(View.GONE);
//                }
//                GlideApp.with(mContext).applyDefaultRequestOptions(GlideUtils.getRequestOptions()).load(RetrofitManager.getApiUrl() + bean.getUrl()).into((ImageView) helper.getView(R.id.icon));
//                break;
//            case TYPE_TITLE:
//                helper.setText(R.id.title, bean.getIconName());
//                break;
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return list == null ? 0 : list.size();
//    }
//
//    public OnItemChildClickListener getOnItemChildClickListener() {
//        return mOnItemChildClickListener;
//    }
//
//    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
//        this.mOnItemChildClickListener = onItemChildClickListener;
//    }
//
//    public int getSpanSize(int position) {
//        ConfigIconItem mainBean = (ConfigIconItem) list.get(position);
//        switch (mainBean.getItemType()) {
//            case TYPE_SERVICE:
//                return 4;
//            case TYPE_TITLE:
//                return 12;
//            default:
//                return 12;
//        }
//    }
//
//    public void reset() {
//        boolean isAll = false;
//        for (int i = 1; i < list.size(); i++) {
//            if (list.get(i).getIconName().equals(mContext.getString(R.string.all_service))) {
//                total_selected = i;
//                isAll = true;
//            }
//            if (!isAll) {
//                list.get(i).setSelect(true);
//            } else {
//                list.get(i).setSelect(false);
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//    public void addSelected(int position) {
//        if (position < list.size()) {
//            int start = total_selected;
//            list.get(position).setSelect(true);
//            for (int i = position; i > start; i--) {
//                Collections.swap(list, i, i - 1);
//            }
//            total_selected++;
//            notifyItemRangeChanged(start, position);
//        }
//    }
//
//    public void delSelected(int position) {
//        if (position < list.size()) {
//            int end = total_selected;
//            list.get(position).setSelect(false);
//            for (int i = position; i < end; i++) {
//                Collections.swap(list, i, i + 1);
//            }
//            total_selected--;
//            notifyItemRangeChanged(position, end);
//        }
//
//    }
//
//    public boolean canDrag(int position) {
//        ConfigIconItem mainBean = (ConfigIconItem) list.get(position);
//        switch (mainBean.getItemType()) {
//            case TYPE_SERVICE:
//                return true;
//            case TYPE_TITLE:
//                return false;
//            default:
//                return false;
//        }
//    }
//
//
//    public interface OnItemChildClickListener {
//        /**
//         * callback method to be invoked when an item in this view has been
//         * click and held
//         *
//         * @param view     The view whihin the ItemView that was clicked
//         * @param position The position of the view int the adapter
//         */
//        void onItemChildClick(View view, int position, boolean isSelected);
//    }
//}
