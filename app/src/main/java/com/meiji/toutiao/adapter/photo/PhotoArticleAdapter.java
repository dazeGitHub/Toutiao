package com.meiji.toutiao.adapter.photo;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meiji.toutiao.R;
import com.meiji.toutiao.bean.photo.PhotoArticleBean;
import com.meiji.toutiao.interfaces.IOnItemClickListener;
import com.meiji.toutiao.utils.SettingsUtil;
import com.meiji.toutiao.utils.TimeUtil;
import com.meiji.toutiao.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Meiji on 2016/12/28.
 */

public class PhotoArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;
    private List<PhotoArticleBean.DataBean> list;
    private Context context;
    private IOnItemClickListener onItemClickListener;

    public PhotoArticleAdapter(Context context) {
        this.context = context;
    }

    public List<PhotoArticleBean.DataBean> getList() {
        return list;
    }

    public void setList(List<PhotoArticleBean.DataBean> list) {
        this.list = new ArrayList<>(list);
    }

    public void setOnItemClickListener(IOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == list.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_photo_article, parent, false);
            return new PhotoViewViewHolder(view, onItemClickListener);
        }
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_footer, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof PhotoViewViewHolder) {
            PhotoViewViewHolder photoViewHolder = (PhotoViewViewHolder) holder;

            PhotoArticleBean.DataBean bean = list.get(position);
            String tv_title = bean.getTitle();
            if (!SettingsUtil.getInstance().getIsNoPhotoMode()) {

                if (!TextUtils.isEmpty(bean.getMedia_avatar_url())) {
                    Glide.with(context).load(bean.getMedia_avatar_url()).crossFade().centerCrop().into(photoViewHolder.iv_media);
                }

                if (bean.getImage_list() != null) {
                    int size = bean.getImage_list().size();
                    String[] ivs = new String[size];
                    for (int i = 0; i < bean.getImage_list().size(); i++) {
                        ivs[i] = bean.getImage_list().get(i).getUrl();
                    }
                    switch (ivs.length) {
                        case 1:
                            Glide.with(context).load(ivs[0]).crossFade().centerCrop().into(photoViewHolder.iv_0);
                            break;
                        case 2:
                            Glide.with(context).load(ivs[0]).crossFade().centerCrop().into(photoViewHolder.iv_0);
                            Glide.with(context).load(ivs[1]).crossFade().centerCrop().into(photoViewHolder.iv_1);
                            break;
                        case 3:
                            Glide.with(context).load(ivs[0]).crossFade().centerCrop().into(photoViewHolder.iv_0);
                            Glide.with(context).load(ivs[1]).crossFade().centerCrop().into(photoViewHolder.iv_1);
                            Glide.with(context).load(ivs[2]).crossFade().centerCrop().into(photoViewHolder.iv_2);
                            break;
                    }
                }
            }
            String tv_source = bean.getSource();
            String tv_datetime = bean.getBehot_time() + "";
            if (!TextUtils.isEmpty(tv_datetime)) {
                tv_datetime = TimeUtil.getTimeStampAgo(tv_datetime);
            }
            photoViewHolder.tv_title.setText(tv_title);
            photoViewHolder.tv_extra.setText(tv_source + " - " + tv_datetime);
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() + 1 : 0;
    }

    private class PhotoViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView iv_media;
        private TextView tv_extra;
        private TextView tv_title;
        private ImageView iv_0;
        private ImageView iv_1;
        private ImageView iv_2;
        private IOnItemClickListener onItemClickListener;

        PhotoViewViewHolder(View itemView, IOnItemClickListener onItemClickListener) {
            super(itemView);
            this.iv_media = (CircleImageView) itemView.findViewById(R.id.iv_media);
            this.tv_extra = (TextView) itemView.findViewById(R.id.tv_extra);
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            this.iv_0 = (ImageView) itemView.findViewById(R.id.iv_0);
            this.iv_1 = (ImageView) itemView.findViewById(R.id.iv_1);
            this.iv_2 = (ImageView) itemView.findViewById(R.id.iv_2);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(view, getLayoutPosition());
            }
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        FooterViewHolder(View itemView) {
            super(itemView);
            this.progressBar = (ProgressBar) itemView.findViewById(R.id.progress_footer);
            int color = SettingsUtil.getInstance().getColor();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
                DrawableCompat.setTint(wrapDrawable, color);
                this.progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
            } else {
                this.progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }
}
