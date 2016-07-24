package testsample.altvr.com.testsample.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import testsample.altvr.com.testsample.R;
import testsample.altvr.com.testsample.util.DatabaseUtil;
import testsample.altvr.com.testsample.util.LogUtil;
import testsample.altvr.com.testsample.util.ScreenUtil;
import testsample.altvr.com.testsample.vo.PhotoVo;

public class ItemsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LogUtil log = new LogUtil(ItemsListAdapter.class);
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_ITEM = 0;

    private static final int INVALID_DIMEN = -1;

    private final ItemListener mListener;
    private final int mImageWidth;


    private List<PhotoVo> mItems;
    private Context mContext;
    private DatabaseUtil mDbUtil;

    public interface ItemListener {
        void itemClicked(ItemViewHolder rowView, int position);
    }

    public ItemsListAdapter(List<PhotoVo> items, ItemListener listener, int imageWidth, Context context) {
        mItems = items;
        mListener = listener;
        mImageWidth = imageWidth;
        mContext = context;
        mDbUtil = new DatabaseUtil(mContext);
    }

    public void setmItems(List<PhotoVo> mItems) {
        this.mItems = mItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_photos_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
          /*
         * YOUR CODE HERE
         *
         * For Part 1a, you should get the proper PhotoVo instance from the mItems collection,
         * image, text, etc, into the ViewHolder (which will be an ItemViewHolder.)
         *
         * For part 1b, you should attach a click listener to the save label so users can save
         * or delete photos from their local db.
         */
        final PhotoVo photoVo = mItems.get(position);
        // Set Tag information
        ((ItemViewHolder) holder).itemName.setText(mContext.getString(R.string.tags) + photoVo.tags);

        // Check if the photo already saved, display UI according to local query result
        if (mDbUtil.getPhoto(photoVo.id) == null) {
            ((ItemViewHolder) holder).saveText.setText(mContext.getString(R.string.save));
        } else {
            ((ItemViewHolder) holder).saveText.setText(mContext.getString(R.string.unsave));
        }

        ((ItemViewHolder) holder).saveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((TextView) view).getText().toString().equalsIgnoreCase(mContext.getString(R.string.save))) {
                    // save photo to local db, update ui
                    if (mDbUtil.saveOrUpdatePhoto(photoVo)) {
                        ((TextView) view).setText(mContext.getString(R.string.unsave));
                        Toast.makeText(mContext, mContext.getString(R.string.saved), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // remove photo from local db, update ui
                    mDbUtil.deletePhoto(photoVo.id);
                    ((TextView) view).setText(mContext.getString(R.string.save));
                    Toast.makeText(mContext, mContext.getString(R.string.unsaved), Toast.LENGTH_SHORT).show();

                }
            }
        });

        // compare screen width and previewWidth to determine which image will be load
        String imgUrl = photoVo.previewURL;
        if (ScreenUtil.getScreenWidth(mContext) > photoVo.previewWidth) {
            imgUrl = mItems.get(position).webformatURL;
        }
        Picasso.with(mContext).load(imgUrl).placeholder(R.color.colorPrimary).into(((ItemViewHolder) holder).itemImage);


        // set share textview listener
        ((ItemViewHolder) holder).share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "I love this photo");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Download from " + photoVo.webformatURL);
                sendIntent.setType("text/plain");
                mContext.startActivity(Intent.createChooser(sendIntent, "Share Image"));

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isImageSizeGiven() {
        return mImageWidth != INVALID_DIMEN;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView itemName;
        public TextView saveText;
        public TextView share;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            saveText = (TextView) itemView.findViewById(R.id.saveText);
            share = (TextView) itemView.findViewById(R.id.share);

        }


    }


}
