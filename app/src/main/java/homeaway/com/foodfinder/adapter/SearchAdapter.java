package homeaway.com.foodfinder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import homeaway.com.foodfinder.R;
import homeaway.com.foodfinder.model.venueModel.Response;
import homeaway.com.foodfinder.model.venueModel.Venue;
import homeaway.com.foodfinder.util.BookmarkPreferences;
import homeaway.com.foodfinder.util.PaginationAdapterCallback;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The application context for getting resources
    private Context context;

    // The list of results from the Foursquare API
//    private Response results;
    private List<Venue> results;

    private OnItemClickedListener mItemClickListener = null;

    //shared preference to persist bookmarks
    private BookmarkPreferences preferences;

    //pagination constants
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private PaginationAdapterCallback mCallback;
    private String errorMsg;

    public SearchAdapter(Context context/*, Response results*/) {
        this.context = context;
//        this.results = results;
        this.mCallback = (PaginationAdapterCallback) context;
        results = new ArrayList<>();
        preferences = BookmarkPreferences.getBookmarkPreferences();
    }

    public List<Venue> getResults() {
        return results;
    }

    public void setResults(List<Venue> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                viewHolder = getLoadingHolder(parent, inflater);
                break;
        }
        return viewHolder;

    }

    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.search_item, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    private RecyclerView.ViewHolder getLoadingHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v2 = inflater.inflate(R.layout.item_progress, parent, false);
        viewHolder = new LoadingViewHolder(v2);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // Sets each view with the appropriate venue details
        Venue venue = results.get(position);

        switch (getItemViewType(position)){
            case ITEM:
                ViewHolder viewHolder = (ViewHolder)  holder;
                //item click listener
                viewHolder.itemView.setTag(position);
                viewHolder.itemView.setOnClickListener(mOnClickListener);

                //load image with picasso
                String url = venue.getCategories().get(0).getIcon().getPrefix();
                if(url.contains("ss3.4sqi.net")){
                    url = url.replace("ss3.4sqi.net", "foursquare.com");
                    url = url + "bg_64" + venue.getCategories().get(0).getIcon().getSuffix();
                }
                Uri uri = Uri.parse(url);
                Log.i("harika", uri.toString());
                Picasso.with(context)
                        .load(uri)
                        .resize(80, 80)
                        .centerCrop()
                        .noPlaceholder()
                        .error(context.getResources().getDrawable(R.drawable.ic_error_black_24dp))
                        .into(viewHolder.icon);

                viewHolder.name.setText(venue.getName());
                viewHolder.category.setText(venue.getCategories().get(0).getName());
                viewHolder.distance.setText((venue.getLocation().getDistance()) + context.getString(R.string.miles));

                //bookmark click listener
                viewHolder.bookmark.setOnClickListener(null);
                clickEvent(viewHolder);

                if(checkBookmarks(venue)) {
                    viewHolder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_black_24dp));
                } else {
                    viewHolder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
                }

                //additional venue details for the map
                viewHolder.id = venue.getId();
                viewHolder.latitude = venue.getLocation().getLat();
                viewHolder.longitude = venue.getLocation().getLng();
                break;
            case LOADING:
                LoadingViewHolder loadingVH = (LoadingViewHolder) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        List<Venue> venues = results;
        if (venues != null) {
            Log.i("harika", "size " + venues.size());
            return venues.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == results.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /**********************************view holders*********************************/

    class ViewHolder extends RecyclerView.ViewHolder  {

        // The venue fields to display
        ImageView icon;
        TextView name;
        TextView category;
        TextView distance;
        ImageView bookmark;

        //details for map
        String id;
        double latitude;
        double longitude;

        ViewHolder(View itemView) {
            super(itemView);

            // Gets the appropriate view for each venue detail
            icon = itemView.findViewById(R.id.search_icon);
            name = itemView.findViewById(R.id.search_name);
            category = itemView.findViewById(R.id.search_category);
            distance = itemView.findViewById(R.id.search_distance);
            bookmark = itemView.findViewById(R.id.search_bookmark);

        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ProgressBar mProgressBar;
        ImageButton mRetryBtn;
        TextView mErrorTxt;
        LinearLayout mErrorLayout;

        LoadingViewHolder(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }

    /****************************************bookmark helper methods****************************************************/

    private void clickEvent(final ViewHolder holder) {
        holder.bookmark.setOnClickListener(view -> {
            //getAdapterPosition returns the adapter position of the item in viewholder
            Venue venue = results.get(holder.getAdapterPosition());
            if(checkBookmarks(venue)) {
                saveBookmark(holder, venue);
            } else {
                deleteBookmark(holder, venue);
            }
        });
    }

    private void saveBookmark(ViewHolder holder, Venue venue) {
        preferences.addBookmark(context, venue);
        Log.i("harika", "bookmark added: " + results.get(holder.getAdapterPosition()).getName());
        holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_black_24dp));
        notifyDataSetChanged();
    }

    private void deleteBookmark(ViewHolder holder, Venue venue) {
        preferences.removeBookmark(context, venue);
        Log.i("harika", "bookmark deleted: " + results.get(holder.getAdapterPosition()).getName());
        holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
        notifyDataSetChanged();

    }

    /**** Checks whether a particular bookmark exists in SharedPreferences *****/
    private boolean checkBookmarks(Venue checkBookmark) {
        boolean check = false;
        List<Venue> favorites = preferences.getBookmarks(context);
        Log.i("harika", "list of bookmarks: " + favorites);
        if (favorites != null) {
            for (Venue bookmark : favorites) {
                if (bookmark.equals(checkBookmark)) {
                    check = true;
                    break;
                }
            }
        }
        Log.i("harika", "boolean: " + check);
        return check;
    }

    /************************************item click listener************************************************/

    public interface OnItemClickedListener {
        void onItemClick(Venue response, int position);
    }

    public void setOnClickedListener(OnItemClickedListener listener) {
        mItemClickListener = listener;
    }

    //custom on item click listener
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();

            if(mItemClickListener != null) {
                mItemClickListener.onItemClick(results.get(position), position);
            }
        }
    };

    /**************************************pagination helper methods*****************************/

    public void add(Venue venue) {
        results.add(venue);
        notifyItemInserted(results.size() - 1);
    }

    public void addAll(List<Venue> moveResults) {
        for (Venue result : moveResults) {
            add(result);
        }
    }

    public void remove(Venue venue) {
        int position = results.indexOf(venue);
        if (position > -1) {
            results.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Venue());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = results.size() - 1;
        Venue result = getItem(position);

        if (result != null) {
            results.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Venue getItem(int position) {
        return results.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(results.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }
}
