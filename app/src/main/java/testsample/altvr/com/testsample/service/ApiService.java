package testsample.altvr.com.testsample.service;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import testsample.altvr.com.testsample.PixabayRetrofitService;
import testsample.altvr.com.testsample.RetrofitAdapter;
import testsample.altvr.com.testsample.events.ApiErrorEvent;
import testsample.altvr.com.testsample.events.PhotosEvent;
import testsample.altvr.com.testsample.util.LogUtil;
import testsample.altvr.com.testsample.vo.PhotoResponseVo;
import testsample.altvr.com.testsample.vo.PhotoVo;

public class ApiService {
    private LogUtil log = new LogUtil(ApiService.class);
    private static String PIXABAY_API_KEY = "2387134-2e9952af7d840c1d7abc947b1";
    private static int MIN_IMAGE_WIDTH = 1000;
    private static int MIN_IMAGE_HEIGHT = 1000;
    private static String IMAGE_TYPE = "photo";

    private PixabayRetrofitService mService;
    private EventBus mEventBus;
    private Context context;

    public ApiService(Context context) {
        mService = RetrofitAdapter.getRestService(context);
        mEventBus = EventBus.getDefault();
        this.context = context;


    }


    /**
     * YOUR CODE HERE
     *
     * For part 1a, you should implement getDefaultPhotos and searchPhotos. These calls should make the proper
     * API calls to Pixabay and post PhotosEvents to the event bus for the fragments to fill themselves in.
     *
     * We provide a Retrofit API adapter here you can use, or you can roll your own using the HTTP library
     * of your choice.
     */

    public void getDefaultPhoto() {

        if(mService==null) {
            mService = RetrofitAdapter.getRestService(context);
        }
        mService.getDefaultPhotos(PIXABAY_API_KEY, MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT, IMAGE_TYPE,
                new Callback<PhotoResponseVo>() {
                    @Override
                    public void success(PhotoResponseVo photoResponseVo, Response response) {
                        if(response.getStatus() ==200) {
                            List<PhotoVo> photoVoList = photoResponseVo.hits;
                            PhotosEvent photosEvent = new PhotosEvent(photoVoList);
                            mEventBus.post(photosEvent);
                        } else {
                            ApiErrorEvent errorEvent = new ApiErrorEvent(response.getReason());
                            mEventBus.post(errorEvent);
                        }

                    }
                    @Override
                    public void failure(RetrofitError error) {
                        ApiErrorEvent errorEvent = new ApiErrorEvent(error.getMessage());
                        mEventBus.post(errorEvent);
                    }
                });

    }

    public void searchPhotos(String query) {

        if(mService==null) {
            mService = RetrofitAdapter.getRestService(context);
        }
        mService.searchPhotos(PIXABAY_API_KEY, query, MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT, IMAGE_TYPE,
                new Callback<PhotoResponseVo>() {
                    @Override
                    public void success(PhotoResponseVo photoResponseVo, Response response) {
                        if (response.getStatus() == 200) {
                            List<PhotoVo> photoVoList = photoResponseVo.hits;
                            PhotosEvent photosEvent = new PhotosEvent(photoVoList);
                            mEventBus.post(photosEvent);
                        } else {
                            ApiErrorEvent errorEvent = new ApiErrorEvent(response.getReason());
                            mEventBus.post(errorEvent);
                        }

                    }
                    @Override
                    public void failure(RetrofitError error) {
                        ApiErrorEvent errorEvent = new ApiErrorEvent(error.getMessage());
                        mEventBus.post(errorEvent);

                    }
                });

    }

}
