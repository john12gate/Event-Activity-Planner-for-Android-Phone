package com.example.eventapp.rest;

import com.example.eventapp.response.AboutUsRP;
import com.example.eventapp.response.AppRP;
import com.example.eventapp.response.CatUploadRP;
import com.example.eventapp.response.CategoryRP;
import com.example.eventapp.response.ContactRP;
import com.example.eventapp.response.DataRP;
import com.example.eventapp.response.EditEventRP;
import com.example.eventapp.response.EventDetailRP;
import com.example.eventapp.response.EventRP;
import com.example.eventapp.response.FaqRP;
import com.example.eventapp.response.FavouriteRP;
import com.example.eventapp.response.GetTicketRP;
import com.example.eventapp.response.HomeRP;
import com.example.eventapp.response.LoginRP;
import com.example.eventapp.response.PrivacyPolicyRP;
import com.example.eventapp.response.ProfileRP;
import com.example.eventapp.response.RegisterRP;
import com.example.eventapp.response.TermsConditionsRP;
import com.example.eventapp.response.TicketBookRP;
import com.example.eventapp.response.TicketDownloadRP;
import com.example.eventapp.response.TicketViewRP;
import com.example.eventapp.response.UploadEventRP;
import com.example.eventapp.response.UserTicketListRP;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiInterface {

    //get app data
    @POST("api.php")
    @FormUrlEncoded
    Call<AppRP> getAppData(@Field("data") String data);

    //get about us
    @POST("api.php")
    @FormUrlEncoded
    Call<AboutUsRP> getAboutUs(@Field("data") String data);

    //get privacy policy
    @POST("api.php")
    @FormUrlEncoded
    Call<PrivacyPolicyRP> getPrivacyPolicy(@Field("data") String data);

    //get terms condition
    @POST("api.php")
    @FormUrlEncoded
    Call<TermsConditionsRP> getTermsCondition(@Field("data") String data);

    //get faq
    @POST("api.php")
    @FormUrlEncoded
    Call<FaqRP> getFaq(@Field("data") String data);

    //login
    @POST("api.php")
    @FormUrlEncoded
    Call<LoginRP> getLogin(@Field("data") String data);

    //register
    @POST("api.php")
    @FormUrlEncoded
    Call<RegisterRP> getRegister(@Field("data") String data);

    //forgot password
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> getForgotPass(@Field("data") String data);

    //login check
    @POST("api.php")
    @FormUrlEncoded
    Call<LoginRP> getLoginDetail(@Field("data") String data);

    //get profile detail
    @POST("api.php")
    @FormUrlEncoded
    Call<ProfileRP> getProfile(@Field("data") String data);

    //edit profile
    @POST("api.php")
    @Multipart
    Call<DataRP> editProfile(@Part("data") RequestBody data, @Part MultipartBody.Part part);

    //update password
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> updatePassword(@Field("data") String data);

    //get contact subject
    @POST("api.php")
    @FormUrlEncoded
    Call<ContactRP> getContactSub(@Field("data") String data);

    //submit contact
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> submitContact(@Field("data") String data);

    //home
    @POST("api.php")
    @FormUrlEncoded
    Call<HomeRP> getHome(@Field("data") String data);

    //category
    @POST("api.php")
    @FormUrlEncoded
    Call<CategoryRP> getCategory(@Field("data") String data);

    //event list
    @POST("api.php")
    @FormUrlEncoded
    Call<EventRP> getEvent(@Field("data") String data);

    //Favourite book
    @POST("api.php")
    @FormUrlEncoded
    Call<FavouriteRP> getFavouriteEvent(@Field("data") String data);

    //event detail
    @POST("api.php")
    @FormUrlEncoded
    Call<EventDetailRP> getEventDetail(@Field("data") String data);

    //event report
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> submitEventReport(@Field("data") String data);

    //view ticket
    @POST("api.php")
    @FormUrlEncoded
    Call<TicketViewRP> viewTicket(@Field("data") String data);

    //download ticket
    @POST("api.php")
    @FormUrlEncoded
    Call<TicketDownloadRP> downloadTicket(@Field("data") String data);

    //user ticket list
    @POST("api.php")
    @FormUrlEncoded
    Call<UserTicketListRP> getUserTicketList(@Field("data") String data);

    //my event list
    @POST("api.php")
    @FormUrlEncoded
    Call<EventRP> getMyEvent(@Field("data") String data);

    //delete event
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> deleteEvent(@Field("data") String data);

    //get ticket
    @POST("api.php")
    @FormUrlEncoded
    Call<GetTicketRP> getEventTicket(@Field("data") String data);

    //booking ticket
    @POST("api.php")
    @FormUrlEncoded
    Call<TicketBookRP> bookingEvent(@Field("data") String data);

    //category list selection
    @POST("api.php")
    @FormUrlEncoded
    Call<CatUploadRP> getCatUpload(@Field("data") String data);

    //upload event
    @POST("api.php")
    @Multipart
    Call<UploadEventRP> uploadEvent(@Part("data") RequestBody data, @Part List<MultipartBody.Part> part);

    //edit event
    @POST("api.php")
    @FormUrlEncoded
    Call<EditEventRP> editEventRP(@Field("data") String data);

    //upload remove gallery image
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> removeGalleryImage(@Field("data") String data);

}
