package com.example.eventapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.bumptech.glide.Glide;
import com.example.eventapp.R;
import com.example.eventapp.activity.Login;
import com.example.eventapp.interFace.FavouriteIF;
import com.example.eventapp.interFace.OnClick;
import com.example.eventapp.item.EventList;
import com.example.eventapp.util.Constant;
import com.example.eventapp.util.Method;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter {

    private final Activity activity;
    private final Method method;
    private final String type;
    private final int columnWidth;
    private final List<EventList> eventLists;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_Ad = -1;

    public EventAdapter(Activity activity, List<EventList> eventLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.eventLists = eventLists;
        method = new Method(activity, onClick);
        columnWidth = method.getScreenWidth();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.event_adapter, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType == VIEW_TYPE_Ad) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_ads, parent, false);
            return new ADViewHolder(v);
        }
        return null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            if (eventLists.get(position).isIs_fav()) {
                viewHolder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
            } else {
                viewHolder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
            }

            viewHolder.imageView.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, columnWidth / 2));
            viewHolder.view.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, columnWidth / 2));

            Glide.with(activity).load(eventLists.get(position).getEvent_banner_thumb())
                    .placeholder(R.drawable.placeholder_banner).into(viewHolder.imageView);

            viewHolder.textViewTitle.setText(eventLists.get(position).getEvent_title());
            String date = eventLists.get(position).getEvent_date();
            String[] separated = date.split(",");
            viewHolder.textViewDay.setText(separated[0]);
            viewHolder.textViewMonth.setText(separated[1]);
            viewHolder.textViewAdd.setText(eventLists.get(position).getEvent_address());

            viewHolder.materialCardView.setOnClickListener(v -> method.click(position, type, eventLists.get(position).getEvent_title(), eventLists.get(position).getId()));

            viewHolder.imageViewFav.setOnClickListener(view -> {
                if (method.isLogin()) {
                    FavouriteIF favouriteIF = (isFavourite, message) -> {
                        if (isFavourite) {
                            viewHolder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
                        } else {
                            viewHolder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
                        }
                    };
                    method.addToFav(eventLists.get(position).getId(), method.userId(), type, position, favouriteIF);
                } else {
                    Method.loginBack = true;
                    activity.startActivity(new Intent(activity, Login.class));
                }
            });

        } else if (holder.getItemViewType() == VIEW_TYPE_Ad) {

            final ADViewHolder viewHolder = (ADViewHolder) holder;
            if (viewHolder.rl_native_ad.getChildCount() == 0) {
                if (Constant.appRP.isNative_ad() && !viewHolder.isAdRequested) {

                    viewHolder.isAdRequested = true;

                    switch (Constant.appRP.getNative_ad_type()) {
                        case Constant.AD_TYPE_ADMOB:
                        case Constant.AD_TYPE_FACEBOOK:

                            NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);

                            AdLoader adLoader = new AdLoader.Builder(activity, Constant.appRP.getNative_ad_id())
                                    .forNativeAd(nativeAd -> {
                                        populateUnifiedNativeAdView(nativeAd, adView);
                                        ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                                        ((ADViewHolder) holder).rl_native_ad.addView(adView);

                                        ((ADViewHolder) holder).card_view.setVisibility(View.VISIBLE);
                                    })
                                    .build();

                            AdRequest.Builder builder = new AdRequest.Builder();
                            if (Method.personalizationAd) {
                                Bundle extras = new Bundle();
                                extras.putString("npa", "1");
                                builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                            }
                            adLoader.loadAd(builder.build());

                            break;
                        case Constant.AD_TYPE_STARTAPP:
                            StartAppNativeAd nativeAd = new StartAppNativeAd(activity);

                            nativeAd.loadAd(new NativeAdPreferences()
                                    .setAdsNumber(1)
                                    .setAutoBitmapDownload(true)
                                    .setPrimaryImageSize(2), new AdEventListener() {
                                @Override
                                public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                    try {
                                        if(nativeAd.getNativeAds().size() > 0) {
                                            RelativeLayout nativeAdView = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.layout_native_ad_startapp, null);

                                            ImageView icon = nativeAdView.findViewById(R.id.icon);
                                            TextView title = nativeAdView.findViewById(R.id.title);
                                            TextView description = nativeAdView.findViewById(R.id.description);
                                            Button button = nativeAdView.findViewById(R.id.button);

                                            icon.setImageBitmap(nativeAd.getNativeAds().get(0).getImageBitmap());
                                            title.setText(nativeAd.getNativeAds().get(0).getTitle());
                                            description.setText(nativeAd.getNativeAds().get(0).getDescription());
                                            button.setText(nativeAd.getNativeAds().get(0).isApp() ? "Install" : "Open");

                                            viewHolder.rl_native_ad.removeAllViews();
                                            viewHolder.rl_native_ad.addView(nativeAdView);
                                            viewHolder.card_view.setVisibility(View.VISIBLE);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailedToReceiveAd(Ad ad) {
                                    viewHolder.isAdRequested = false;
                                }
                            });
                            break;
                        case Constant.AD_TYPE_APPLOVIN:
                            MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(Constant.appRP.getNative_ad_id(), activity);
                            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                @Override
                                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                    nativeAdView.setPadding(0, 0, 0, 10);
                                    nativeAdView.setBackgroundColor(Color.WHITE);
                                    viewHolder.rl_native_ad.removeAllViews();
                                    viewHolder.rl_native_ad.addView(nativeAdView);
                                    viewHolder.card_view.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                    viewHolder.isAdRequested = false;
                                }

                                @Override
                                public void onNativeAdClicked(final MaxAd ad) {
                                }
                            });

                            nativeAdLoader.loadAd();
                            break;
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return eventLists.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == eventLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_TYPE_LOADING;
        } else if (eventLists.get(position) == null) {
            return VIEW_TYPE_Ad;
        }
        return VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final MaterialCardView materialCardView;
        private final ImageView imageView;
        private final ImageView imageViewFav;
        private final MaterialTextView textViewTitle;
        private final MaterialTextView textViewDay;
        private final MaterialTextView textViewMonth;
        private final MaterialTextView textViewAdd;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view_subCat_adapter);
            materialCardView = itemView.findViewById(R.id.cardView_event_adapter);
            imageView = itemView.findViewById(R.id.imageView_event_adapter);
            imageViewFav = itemView.findViewById(R.id.imageView_fav_event_adapter);
            textViewTitle = itemView.findViewById(R.id.textView_title_event_adapter);
            textViewDay = itemView.findViewById(R.id.textView_day_event_adapter);
            textViewMonth = itemView.findViewById(R.id.textView_month_event_adapter);
            textViewAdd = itemView.findViewById(R.id.textView_add_event_adapter);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar_loading);
        }
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {
        private CardView card_view;
        private final RelativeLayout rl_native_ad;
        private boolean isAdRequested = false;

        private ADViewHolder(View view) {
            super(view);
            card_view = view.findViewById(R.id.card_view);
            rl_native_ad = view.findViewById(R.id.rl_native_ad);
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }
}