package com.jintoufs.zj.transfercabinet.net;

import com.jintoufs.zj.transfercabinet.model.bean.CertUser;
import com.jintoufs.zj.transfercabinet.model.bean.CabinetInfoBean;
import com.jintoufs.zj.transfercabinet.model.bean.CertificateVo;
import com.jintoufs.zj.transfercabinet.model.bean.ResponseInfo;
import com.jintoufs.zj.transfercabinet.model.bean.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by zoujiang on 2017/2/15.
 */

public interface ApiService {
    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    @GET("api/common/login")
    Call<ResponseInfo<User>> login(@Query("userName") String username, @Query("password") String password);

    /**
     * 获取证件用户信息
     *
     * @param cUserId
     * @return
     */
    @GET("api/common/getCUserById")
    Call<ResponseInfo<CertUser>> getCUserById(@Query("certUserId") String cUserId);

    /**
     * 根据证件号查询证件详情
     *
     * @param number
     * @return
     */
    @GET("api/common/getCertificateByNumber")
    Call<ResponseInfo<CertificateVo>> getCertificateByNumber(@Query("number") String number);

    /**
     * 短信验证码验证,通过返回用户id
     *
     * @param msgCode
     * @return
     */
    @GET("api/transferCabinet/validateMessageCode")
    Call<ResponseInfo<String>> validateMessageCode(@Query("msgCode") String msgCode);

    /**
     * 取证提交 （申领人 userId置空）
     *
     * @param certificateOpVoList
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST("api/transferCabinet/tccOutSubmit")
    Call<ResponseInfo<String>> tccOutSubmit(@Field("certificateOpVoList") String certificateOpVoList,
                                            @Field("userId") String userId);

    /**
     * 存证提交（申领人 userId置空）
     *
     * @param certificateOpVoList
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST("api/transferCabinet/tccInSubmit")
    Call<ResponseInfo<String>> tccInSubmit(@Field("certificateOpVoList") String certificateOpVoList,
                                           @Field("userId") String userId);

    /**
     * 通过IP获取交接柜信息
     *
     * @param ipAddress
     * @return
     */
    @GET("api/transferCabinet/getTransferCabinetByIp")
    Call<ResponseInfo<CabinetInfoBean>> getTransferCabinetByIp(@Query("ipAddress") String ipAddress);

    /**
     * 交接柜维护
     *
     * @param userId
     * @param opType
     * @param cabinetSerialNo
     * @param locationCode
     * @return
     */
    @FormUrlEncoded
    @POST("api/transferCabinet/tccMaintainSubmit")
    Call<ResponseInfo<String>> tccMaintainSubmit(@Field("userId") String userId,
                                                 @Field("opType") String opType,
                                                 @Field("cabinetSerialNo") String cabinetSerialNo,
                                                 @Field("locationCode") String locationCode);
}
