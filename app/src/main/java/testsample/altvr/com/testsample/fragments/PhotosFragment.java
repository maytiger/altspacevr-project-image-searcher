package testsample.altvr.com.testsample.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import testsample.altvr.com.testsample.R;
import testsample.altvr.com.testsample.adapter.ItemsListAdapter;
import testsample.altvr.com.testsample.events.ApiErrorEvent;
import testsample.altvr.com.testsample.events.PhotosEvent;
import testsample.altvr.com.testsample.service.ApiService;
import testsample.altvr.com.testsample.util.DatabaseUtil;
import testsample.altvr.com.testsample.util.LogUtil;
import testsample.altvr.com.testsample.vo.PhotoVo;

public class PhotosFragment extends Fragment{
    private LogUtil log = new LogUtil(PhotosFragment.class);
    private LinearLayout fetchingItems;
    private RecyclerView itemsListRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService mService;

    private ArrayList<PhotoVo> mItemsData = new ArrayList<>();
    private ItemsListAdapter mListAdapter;
    private DatabaseUtil mDatabaseUtil;
    private EditText[] editTexts = new EditText[4];
    private Button btTry, btHint;
    private String keyWord;



    public static PhotosFragment newInstance() {
        return new PhotosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mService = new ApiService(getActivity());
        mDatabaseUtil = new DatabaseUtil(getActivity());
        setupViews();
        mService.getDefaultPhoto();

    }

    private void initViews(View view) {
        fetchingItems = (LinearLayout) view.findViewById(R.id.listEmptyView);
        itemsListRecyclerView = (RecyclerView) view.findViewById(R.id.photosListRecyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        EditText et1 = (EditText)view.findViewById(R.id.et_input_1);
        EditText et2 = (EditText)view.findViewById(R.id.et_input_2);
        EditText et3 = (EditText)view.findViewById(R.id.et_input_3);
        EditText et4 = (EditText)view.findViewById(R.id.et_input_4);
        editTexts[0] = et1;
        editTexts[1] = et2;
        editTexts[2] = et3;
        editTexts[3] = et4;
        btHint = (Button)view.findViewById(R.id.bt_input_0);
        btTry = (Button)view.findViewById(R.id.bt_input_5);
    }

    private void setupViews() {
        fetchingItems.setVisibility(View.VISIBLE);
        setupItemsList();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mService.getDefaultPhoto();


            }
        });
        for(final EditText et: editTexts) {
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 0) {
                        et.clearFocus();
                        EditText nextEditText = getNextEditText();
                        if (nextEditText != null) {
                            nextEditText.requestFocus();
                        } else {
                            btHint.setVisibility(View.GONE);
                            btTry.setVisibility(View.VISIBLE);
                        }
                    } else {
                        btHint.setVisibility(View.VISIBLE);
                        btTry.setVisibility(View.GONE);
                    }

                }
            });
        }
        btHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    showHint(editTexts, keyWord);


            }
        });

        btTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuffer answer = new StringBuffer();
                for (EditText et : editTexts) {
                    answer.append(et.getText().toString());
                }
                if (answer.toString().length() == 4 && answer.toString().equalsIgnoreCase(keyWord)) {
                    Toast.makeText(getContext(), "Congratulation!, You got it", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Sorry, please try it again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        EventBus.getDefault().register(this);
    }

    private EditText getNextEditText() {
        for(EditText et: editTexts) {
            if(et.length() == 0) {
                return et;
            }
        }
        return null;
    }

    private void showHint(EditText[] editTexts, String keyWord) {

        for(int i=0; i<editTexts.length;i++) {
            editTexts[i].setText("");
            editTexts[i].setTextColor(getResources().getColor(R.color.colorPrimary));
            editTexts[i].setText(String.valueOf(keyWord.charAt(i)));
        }
    }

    private void setupItemsList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        itemsListRecyclerView.setLayoutManager(linearLayoutManager);
        itemsListRecyclerView.setHasFixedSize(true);
        mListAdapter = new ItemsListAdapter(mItemsData, new ItemClickedListener(), getResources().getDisplayMetrics().widthPixels, getContext());
        itemsListRecyclerView.setAdapter(mListAdapter);
    }

    public void guessWord() {

        for(EditText et: editTexts) {
            et.setText("");
        }
        String [] keywordArray = getResources().getStringArray(R.array.guess_words);
        Random random = new Random();
        keyWord = keywordArray[random.nextInt(keywordArray.length)];
        mService.searchPhotos(keyWord);
        int order = random.nextInt(editTexts.length);
        editTexts[order].setTextColor(getResources().getColor(R.color.colorPrimary));
        editTexts[order].setText(String.valueOf(keyWord.charAt(order)));

    }

    private class ItemClickedListener implements ItemsListAdapter.ItemListener {

        @Override
        public void itemClicked(ItemsListAdapter.ItemViewHolder rowView, int position) {

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mItemsData.clear();
        mListAdapter = null;
    }

    @Subscribe
    public void onEvent(PhotosEvent event) {
        /**
         * YOUR CODE HERE
         *
         * This will be the event posted via the EventBus when a photo has been fetched for display.
         *
         * For part 1a you should update the data for this fragment (or notify the user no results
         * were found) and redraw the list.
         *
         * For part 2b you should update this to handle the case where the user has saved photos.
         */

        fetchingItems.setVisibility(View.GONE);
        if(mListAdapter != null) {
            mListAdapter.setmItems(event.data);
            mListAdapter.notifyDataSetChanged();
        }

        if(swipeRefreshLayout!= null) {
            swipeRefreshLayout.setRefreshing(false);
        }


    }

    @Subscribe
    public void onEvent(ApiErrorEvent event) {
        /**
         * YOUR CODE HERE
         *
         * This will be the event posted via the EventBus when an API error has occured.
         *
         * For part 1a you should clear the fragment and notify the user of the error.
         */
        fetchingItems.setVisibility(View.GONE);
        if(mListAdapter != null) {
            // fetch remote photo failure, fetch the local photo
            List<PhotoVo> photoList = mDatabaseUtil.getAllPhoto();
            if(photoList != null && photoList.size() >0) {
                mListAdapter.setmItems(mDatabaseUtil.getAllPhoto());
                mListAdapter.notifyDataSetChanged();
            }
        }

        if(swipeRefreshLayout!= null) {
            swipeRefreshLayout.setRefreshing(false);
        }

        // display error information
        Snackbar.make(getActivity().findViewById(android.R.id.content), event.errorDescription, Snackbar.LENGTH_LONG)
                .show();
    }

}
