//package com.mbbank.ma.service.external.impl;
//
//import com.mbbank.ma.common.AppProperties;
//import com.mbbank.ma.common.ErrConstans;
//import com.mbbank.ma.common.UsersContext;
//import com.mbbank.ma.common.enums.*;
//import com.mbbank.ma.entity.*;
//import com.mbbank.ma.model.UserInfoModel;
//import com.mbbank.ma.model.email.SendMailResModel;
//import com.mbbank.ma.model.intergration.email.CreateEmailResponse;
//import com.mbbank.ma.model.intergration.merchant.GetMerchantBasicInfoOutput;
//import com.mbbank.ma.model.pass.ChangePassReqModel;
//import com.mbbank.ma.model.pass.ChangePassResModel;
//import com.mbbank.ma.model.user.*;
//import com.mbbank.ma.repository.*;
//import com.mbbank.ma.service.external.LoginHistoryService;
//import com.mbbank.ma.service.external.UsersService;
//import com.mbbank.ma.service.integration.impl.MerchantXMMCallBack;
//import com.mbbank.ma.service.integration.impl.SendMailService;
//import com.mbbank.ma.service.integration.impl.common.CallBackRequest;
//import com.mbbank.ma.service.s3.S3ClientImp;
//import com.mbbank.ma.util.*;
//import com.mbbank.ma.util.mapper.ModelMapperUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.text.MessageFormat;
//import java.util.*;
//
//@Servicepublic
//class UsersServiceImpl extends BaseObject implements UsersService {
//    @Autowired
//    private UsersContext usersContext;
//    @Autowired
//    private UsersRepository usersRepo;
//    @Autowired
//    private UserDetailRepository userDetailRepository;
//    @Autowired
//    private EmailTemplateRepository emailTemplateRepository;
//    @Autowired
//    private TokenInfoRepository tokenInfoRepo;
//    @Autowired
//    private LoginHistoryRepository loginHistoryRepo;
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//    @Autowired
//    private DeviceLoginRepository deviceLoginRepository;
//    @Autowired
//    private StringHttpMessageConverter stringHttpMessageConverter;
//    @Autowired
//    private SendMailService sendMailService;
//    @Autowired
//    private AppProperties appProperties;
//    @Autowired
//    private MerchantXMMCallBack merchantXMMCallBack;
//    @Autowired
//    private S3ClientImp s3ClientImp;
//    @Autowired
//    private LoginHistoryService loginHistoryService;
//
//    private CheckLoginFailModel checkLoginFail(Boolean loginFailed, LoginHistoryEntity loginHis, String username) {
//        CheckLoginFailModel rs = new CheckLoginFailModel();
//        rs.setErrConstans(ErrConstans.LOGIN_ERROR_002);
//        rs.setCountFailed(EPasswordThreshold.THRESHOLD_MIN.value);
//        if (loginFailed) {
//            Long countFailed = loginHis.getLoginFail() + 1L;
//            rs.setCountFailed(countFailed);
//            if (Objects.equals(countFailed, EPasswordThreshold.THRESHOLD_LOCK.value)) {
//                khoa tai khoan lai
//                loginHis.setLockUntil(DateTimeUtils.addTime(new Date(), EPasswordThreshold.LOCK_DURATION.value.intValue()));
//                rs.setErrConstans(ErrConstans.LOGIN_ERROR_001);
//            } else {
//                if (Objects.equals(countFailed, EPasswordThreshold.THRESHOLD_WARN_HIGH.value) || Objects.equals(countFailed, EPasswordThreshold.THRESHOLD_WAIT_LOCK.value)) {
//                    rs.setErrConstans(ErrConstans.LOGIN_ERROR_003);
//                }
//            } loginHis.setLoginFail(countFailed);
//            loginHistoryRepo.save(loginHis);
//        } else {
//            loginHistoryService.saveLoginHistory(username, 1L, ELoginStatus.FAILED.value, null, ELoginHisType.LOGIN.value);
//        } return rs;
//    }
//
//    @Override
//    public ChangePassResModel changePassword(ChangePassReqModel model) {/**         * B1: validate input         * B2, 3: check token info and path role, path description in token info         * B4: check account active status         * B5: check input current password (if more than 5 times failed, lock account 15m)         * B6: check ole password and new password (must be different)         * B7: check merchant active status         * B8: update password (if success return isLogout = 1)         */
//        List<LoginHistoryEntity> historys = loginHistoryRepo.findByUserNameAndTypeOrderByCreateDateDesc(username, ELoginHisType.LOGIN.value);
//        Boolean loginFailed = false;
//        LoginHistoryEntity loginHis = null;
//        if (!historys.isEmpty()) {
//            loginHis = historys.getFirst();
//            if (Objects.equals(ELoginStatus.FAILED.value, loginHis.getStatus())) {
//                loginFailed = true;
//                if (Objects.equals(loginHis.getLoginFail(), EPasswordThreshold.THRESHOLD_LOCK.value)) {
//                    if (loginHis.getLockUntil().compareTo(new Date()) > 0) {
//                        dang bi khoa tam thoi throw new MBException(ErrConstans.LOGIN_ERROR_001);
//                    } else {                /**                 * Mo khoa de cho dang nhap tiep                 * update fail = 0, updae lock_unti = null => tiep tuc check login                 */
//                        loginHis.setLoginFail(0L);
//                        loginHis.setLockUntil(null);
//                        loginHistoryRepo.save(loginHis);
//                    }
//                }
//            }
//        }
//
//        String clientMessageId = CommonUtils.getCurrentClientMessageId();
//        logger.info("[{}] Get changePasswordReqModel data = {}", clientMessageId, JsonUtil.toJson(model));
//        if (!StringBaseUtils.inputValid(model.getCurrentPw(), null) || !StringBaseUtils.inputValid(model.getNewPw(), null)) {
//            throw new MBException(ErrConstans.INPUT_ERROR_001);
//        }
//        logger.info("[{}] Start query usersEntity ", clientMessageId);
//        UsersEntity usersEntity = usersRepo.findByUserName(usersContext.getCurrentUserName());
//        logger.info("[{}] Query usersEntity end data = {} ", clientMessageId, JsonUtil.toJson(usersEntity));
//        checkUserIsActive(usersEntity);
//        logger.info("[{}] Start query tokenInfoEntity ", clientMessageId);
//        TokenInfoEntity tokenInfo = tokenInfoRepo.findFirstByUserIdOrderByCreateDateDesc(usersEntity.getId());
//        logger.info("[{}} Query tokenInfoEntity end data = {} ", clientMessageId, JsonUtil.toJson(tokenInfo));
//        logger.info("[{}] Start query loginHistoryEntity ", clientMessageId);
//        List<LoginHistoryEntity> loginHistoryEntities = loginHistoryRepo.findByUserNameAndTypeOrderByCreateDateDesc(usersEntity.getUserName(), ELoginHisType.CHANGE_PASSWORD.value);
//        logger.info("[{}] Query loginHistoryEntity end data = {} ", clientMessageId, JsonUtil.toJson(loginHistoryEntities));
//        LoginHistoryEntity loginHis = null;
//        if (!loginHistoryEntities.isEmpty()) {
//            LoginHistoryEntity hisTemp = loginHistoryEntities.getFirst();
//            if (Objects.equals(ELoginStatus.FAILED.value, hisTemp.getStatus())) {
//                loginHis = hisTemp;
//            }
//        }
//        if (!passwordEncoder.matches(model.getCurrentPw(), usersEntity.getUserPass())) {
//            if (loginHis == null) {
//                loginHis = loginHistoryService.saveLoginHistory(usersEntity.getUserName(), 0L, ELoginStatus.FAILED.value, null, ELoginHisType.CHANGE_PASSWORD.value);
//            }
//            Long countFail = loginHis.getLoginFail() + EPasswordThreshold.THRESHOLD_MIN.value;
//            loginHis.setLoginFail(countFail);
//            if (countFail >= EPasswordThreshold.THRESHOLD_LOCK.value) {
//                loginHis.setLoginFail(0L);
//            }
//            loginHis.setStatus(ELoginStatus.FAILED.value);
//            loginHis.setType(EUserActivityType.CHANGE_PASSWORD.value);
//            logger.info("[{}] Start save loginHistoryEntity data = {}", clientMessageId, JsonUtil.toJson(loginHis));
//            loginHistoryRepo.save(loginHis);
//            logger.info("[{}] Save loginHistoryEntity end ", clientMessageId);
//            if (countFail >= EPasswordThreshold.THRESHOLD_LOCK.value) {
//                loginHistoryService.saveLoginHistory(usersEntity.getUserName(), EPasswordThreshold.THRESHOLD_LOCK.value, ELoginStatus.FAILED.value, DateTimeUtils.addTime(new Date(), EPasswordThreshold.LOCK_DURATION.value.intValue()), ELoginHisType.LOGIN.value);
//                tokenInfo.setIsLogout(ELogoutStatus.LOGGED_OUT.value);
//                logger.info("[{}] Start save tokenInfoEntity data = {}", clientMessageId, JsonUtil.toJson(tokenInfo));
//                tokenInfoRepo.save(tokenInfo);
//                logger.info("[{}] Save loginHistoryEntity end ", clientMessageId);
//                throw new MBException(ErrConstans.CPW_ERROR_001);
//            } else if (Objects.equals(EPasswordThreshold.THRESHOLD_WARN_HIGH.value, loginHis.getLoginFail()) || Objects.equals(EPasswordThreshold.THRESHOLD_WAIT_LOCK.value, loginHis.getLoginFail())) {
//                String message = String.format(ErrConstans.CPW_ERROR_002.getSoaErrorDesc(), loginHis.getLoginFail());
//                throw new MBException(ErrConstans.CPW_ERROR_002.getSoaErrorCode(), message);
//            } else {
//                throw new MBException(ErrConstans.CPW_ERROR_003);
//            }
//        }
//        if (Objects.equals(model.getCurrentPw(), model.getNewPw())) {
//            throw new MBException(ErrConstans.CPW_ERROR_004);
//        }
//        if (!CommonUtils.isNullObject(loginHis.getLockUntil())) {
//            throw new MBException(ErrConstans.CPW_ERROR_001);
//        }
//        MM CALLBACK CallBackRequest callBackRequest = new CallBackRequest();
//        callBackRequest.setClientMessageId(clientMessageId);
//        callBackRequest.setData(usersEntity.getMerchantId());
//        ResponseData<GetMerchantBasicInfoOutput> merchantMerchantInfo = merchantXMMCallBack.getMerchantBasicInfo(callBackRequest);
//        GetMerchantBasicInfoOutput getMerchantBasicInfoOutput = merchantMerchantInfo.getData();
//        if (getMerchantBasicInfoOutput == null || !Objects.equals(EMerchantStatus.ACTIVE.message, getMerchantBasicInfoOutput.getStatus()))
//            throw new MBException(ErrConstans.LOGIN_ERROR_009);
//        usersEntity.setUserPass(passwordEncoder.encode(model.getNewPw()));
//        logger.info("[{}] Save loginHistoryEntity end ", clientMessageId);
//        usersEntity.setIsChangePw(EChangePassword.CHANGED_PASSWORD.value);
//        usersEntity.setLastChangePw(new Date());
//        logger.info("[{}] Start save usesEntity data = {}", clientMessageId, JsonUtil.toJson(usersEntity));
//        usersRepo.save(usersEntity);
//        logger.info("[{}] Save usesEntity end ", clientMessageId);
//        tokenInfo.setIsLogout(ELogoutStatus.LOGGED_OUT.value);
//        logger.info("[{}] Start save tokenInfoEntity data = {}", clientMessageId, JsonUtil.toJson(tokenInfo));
//        tokenInfoRepo.save(tokenInfo);
//        logger.info("[{}] Save tokenInfoEntity end ", clientMessageId);
//        ChangePassResModel changePassResModel = new ChangePassResModel();
//        changePassResModel.setIsLogout(tokenInfo.getIsLogout());
//        logger.info("Response changePassword data = {}", JsonUtil.toJson(changePassResModel));
//        loginHistoryService.saveLoginHistory(usersEntity.getUserName(), 0L, ELoginStatus.SUCCESS.value, null, ELoginHisType.CHANGE_PASSWORD.value);
//        return changePassResModel;
//    }
//
//    @Override
//    public ChangePassResModel changeNewPassword(ChangePassReqModel model) {
//        String clientMessageId = CommonUtils.getCurrentClientMessageId();
//        logger.info("[{}] Get changePasswordReqModel data = {}", clientMessageId, JsonUtil.toJson(model));
//        if (!StringBaseUtils.inputValid(model.getCurrentPw(), null) || !StringBaseUtils.inputValid(model.getNewPw(), null)) {
//            throw new MBException(ErrConstans.INPUT_ERROR_001);
//        }
//        logger.info("[{}] Start query usersEntity ", clientMessageId);
//        UsersEntity usersEntity = usersRepo.findByUserName(usersContext.getCurrentUserName());
//        logger.info("[{}] Query usersEntity end data = {} ", clientMessageId, JsonUtil.toJson(usersEntity));
//        checkUserIsActive(usersEntity);
//        logger.info("[{}] Start query tokenInfoEntity ", clientMessageId);
//        TokenInfoEntity tokenInfo = tokenInfoRepo.findFirstByUserIdOrderByCreateDateDesc(usersEntity.getId());
//        logger.info("[{}} Query tokenInfoEntity end data = {} ", clientMessageId, JsonUtil.toJson(tokenInfo));
//        logger.info("[{}] Start query loginHistoryEntity ", clientMessageId);
//        List<LoginHistoryEntity> loginHistoryEntities = loginHistoryRepo.findByUserNameAndTypeOrderByCreateDateDesc(usersEntity.getUserName(), ELoginHisType.CHANGE_PASSWORD.value);
//        logger.info("[{}] Query loginHistoryEntity end data = {} ", clientMessageId, JsonUtil.toJson(loginHistoryEntities));
//        LoginHistoryEntity loginHis = null;
//        if (!loginHistoryEntities.isEmpty()) {
//            LoginHistoryEntity hisTemp = loginHistoryEntities.getFirst();
//            if (Objects.equals(ELoginStatus.FAILED.value, hisTemp.getStatus())) {
//                loginHis = hisTemp;
//            }
//        }
//        if (!passwordEncoder.matches(model.getCurrentPw(), usersEntity.getUserPass())) {
//            if (loginHis == null) {
//                loginHis = loginHistoryService.saveLoginHistory(usersEntity.getUserName(), 0L, ELoginStatus.FAILED.value, null, ELoginHisType.CHANGE_PASSWORD.value);
//            }
//            if (Objects.equals(loginHis.getLoginFail(), EPasswordThreshold.THRESHOLD_WAIT_LOCK.value)) {
//                loginHis.setLoginFail(0L);
//                tokenInfo.setIsLogout(ELogoutStatus.LOGGED_OUT.value);
//                logger.info("[{}] Start save tokenInfoEntity data = {}", clientMessageId, JsonUtil.toJson(tokenInfo));
//                loginHistoryRepo.save(loginHis);
//                tokenInfoRepo.save(tokenInfo);
//                logger.info("[{}] Save loginHistoryEntity end ", clientMessageId);
//                throw new MBException(ErrConstans.CPW_ERROR_005);
//            } else {
//                loginHis.setLoginFail(loginHis.getLoginFail() + EPasswordThreshold.THRESHOLD_MIN.value);
//                loginHis.setStatus(ELoginStatus.FAILED.value);
//                loginHis.setType(EUserActivityType.CHANGE_PASSWORD.value);
//                logger.info("[{}] Start save loginHistoryEntity data = {}", clientMessageId, JsonUtil.toJson(loginHis));
//                loginHistoryRepo.save(loginHis);
//                logger.info("[{}] Save loginHistoryEntity end ", clientMessageId);
//                throw new MBException(ErrConstans.CPW_ERROR_003);
//            }
//        }
//        if (Objects.equals(model.getCurrentPw(), model.getNewPw())) {
//            throw new MBException(ErrConstans.CPW_ERROR_004);
//        }
//        MM CALLBACK CallBackRequest callBackRequest = new CallBackRequest();
//        callBackRequest.setClientMessageId(clientMessageId);
//        callBackRequest.setData(usersEntity.getMerchantId());
//        ResponseData<GetMerchantBasicInfoOutput> merchantMerchantInfo = merchantXMMCallBack.getMerchantBasicInfo(callBackRequest);
//        GetMerchantBasicInfoOutput getMerchantBasicInfoOutput = merchantMerchantInfo.getData();
//        if (getMerchantBasicInfoOutput == null || !Objects.equals(EMerchantStatus.ACTIVE.message, getMerchantBasicInfoOutput.getStatus()))
//            throw new MBException(ErrConstans.LOGIN_ERROR_009);
//        usersEntity.setUserPass(passwordEncoder.encode(model.getNewPw()));
//        usersEntity.setIsChangePw(EChangePassword.CHANGED_PASSWORD.value);
//        usersEntity.setLastChangePw(new Date());
//        logger.info("[{}] Start save usesEntity data = {}", clientMessageId, JsonUtil.toJson(usersEntity));
//        usersRepo.save(usersEntity);
//        logger.info("[{}] Save usesEntity end ", clientMessageId);
//        tokenInfo.setIsLogout(ELogoutStatus.LOGGED_OUT.value);
//        logger.info("[{}] Start save tokenInfoEntity data = {}", clientMessageId, JsonUtil.toJson(tokenInfo));
//        tokenInfoRepo.save(tokenInfo);
//        logger.info("[{}] Save tokenInfoEntity end ", clientMessageId);
//        ChangePassResModel changePassResModel = new ChangePassResModel();
//        changePassResModel.setIsLogout(tokenInfo.getIsLogout());
//        logger.info("Response changePassword data = {}", JsonUtil.toJson(changePassResModel));
//        loginHistoryService.saveLoginHistory(usersEntity.getUserName(), 0L, ELoginStatus.SUCCESS.value, null, ELoginHisType.CHANGE_PASSWORD.value);
//        return changePassResModel;
//    }
//
//    @Override
//    public ResUserUpdate updateUser(ReqUpdateUserModel request) {        /* B4: Check thông tin đầu vào */
//        String clientMessageId = CommonUtils.getCurrentClientMessageId();
//        logger.info("[{}] Get updateUserReqModel data = {}", clientMessageId, JsonUtil.toJson(request));
//        Long userId = usersContext.getUserId();
//        if (request == null || userId == null) {
//            throw new MBException(ErrConstans.INPUT_ERROR_001);
//        }        /* B7: Check user tồn tại */
//        logger.info("[{}] Start query userDetailEntity ", clientMessageId);
//        UserDetailEntity user = userDetailRepository.findByUserId(userId);
//        logger.info("[{}] Query userDetailEntity end data = {}", clientMessageId, JsonUtil.toJson(user));        /* B8: Check trạng thái tk */
//        logger.info("[{}] Start query usersEntity ", clientMessageId);
//        UsersEntity usersEntity = usersRepo.findById(userId).orElseThrow(() -> new MBException(ErrConstans.USERID_ERROR_001.getSoaErrorCode(), ErrConstans.USERID_ERROR_001.getSoaErrorDesc()));
//        logger.info("[{}] Query usersEntity end data = {}", clientMessageId, JsonUtil.toJson(usersEntity));
//        checkUserIsActive(usersEntity);        /* B9: Check trùng sđt */
//        if (!Objects.equals(user.getPhoneNumber(), request.getPhoneNumber()) && userDetailRepository.existsByPhoneNumber(request.getPhoneNumber())) {
//            throw new MBException(ErrConstans.UPDATE_USER_ERROR_001);
//        }        /* B11: Check merchantID tồn tại*/
//        CallBackRequest callBackRequest = new CallBackRequest();
//        callBackRequest.setClientMessageId(clientMessageId);
//        callBackRequest.setData(usersEntity.getMerchantId());
//        ResponseData<GetMerchantBasicInfoOutput> merchantMerchantInfo = merchantXMMCallBack.getMerchantBasicInfo(callBackRequest);
//        GetMerchantBasicInfoOutput getMerchantBasicInfoOutput = merchantMerchantInfo.getData();
//        if (getMerchantBasicInfoOutput == null || !Objects.equals(EMerchantStatus.ACTIVE.message, getMerchantBasicInfoOutput.getStatus()))
//            throw new MBException(ErrConstans.LOGIN_ERROR_009);        /* B12: Check thông tin có thay đổi không */
//        if (Objects.equals(user.getFullName(), request.getFullName()) && Objects.equals(user.getPhoneNumber(), request.getPhoneNumber()) && Objects.equals(user.getDateOfBirth(), request.getDateOfBirth())) {
//            throw new MBException(ErrConstans.NO_CHANGE);
//        }        /* B13: Cập nhật thông tin */
//        try {
//            user.setFullName(request.getFullName());
//            user.setPhoneNumber(request.getPhoneNumber());
//            user.setDateOfBirth(request.getDateOfBirth());
//            user.setUpdateBy(usersEntity.getUserName());
//            user.setUpdateDate(new Date());
//            logger.info("[{}] Start save userDetailEntity data = {}", clientMessageId, JsonUtil.toJson(user));
//            userDetailRepository.save(user);
//            logger.info("[{}] Save userDetailEntity end ", clientMessageId);
//            ResUserUpdate responseData = ModelMapperUtils.toObject(user, ResUserUpdate.class);
//            responseData.setIsActive(usersEntity.getIsActive());
//            responseData.setUserName(usersEntity.getUserName());
//            logger.info("[{}] Response updateUser data = {}", clientMessageId, JsonUtil.toJson(responseData));
//            return responseData;
//        } catch (Exception e) {
//            throw new MBException(ErrConstans.USER_ERROR_001);
//        }
//    }
//
//    @Override
//    public ResUserInfo getUserProfile() throws IOException {        /* B3: Check thông tin đầu vào */
//        String clientMessageId = CommonUtils.getCurrentClientMessageId();
//        Long userId = usersContext.getUserId();
//        if (userId == null) {
//            throw new MBException(ErrConstans.INPUT_ERROR_001);
//        }        /* B6: Check user tồn tại */
//        logger.info("[{}] Start query userDetailEntity ", clientMessageId);
//        UserDetailEntity user = userDetailRepository.findByUserId(userId);
//        logger.info("[{}] Query userDetailEntity end data = {}", clientMessageId, JsonUtil.toJson(user));        /* B7: Check trạng thái tk */
//        logger.info("[{}] Start query usersEntity ", clientMessageId);
//        UsersEntity usersEntity = usersRepo.findById(userId).orElseThrow(() -> new MBException(ErrConstans.USERID_ERROR_001));
//        logger.info("[{}] Query usersEntity end data = {}", clientMessageId, JsonUtil.toJson(usersEntity));
//        if (Objects.equals(EActive.IN_ACTIVE.value, usersEntity.getIsActive())) {
//            throw new MBException(ErrConstans.LOGIN_ERROR_006);
//        }        /* B8: Check trạng thái merchantID */        /* B9: Check merchantID tồn tại*/
//        CallBackRequest callBackRequest = new CallBackRequest();
//        callBackRequest.setClientMessageId(clientMessageId);
//        callBackRequest.setData(usersEntity.getMerchantId());
//        ResponseData<GetMerchantBasicInfoOutput> merchantMerchantInfo = merchantXMMCallBack.getMerchantBasicInfo(callBackRequest);
//        GetMerchantBasicInfoOutput getMerchantBasicInfoOutput = merchantMerchantInfo.getData();
//        if (getMerchantBasicInfoOutput == null || !Objects.equals(EMerchantStatus.ACTIVE.message, getMerchantBasicInfoOutput.getStatus()))
//            throw new MBException(ErrConstans.LOGIN_ERROR_009);
//        ResUserInfo responseData = ModelMapperUtils.toObject(user, ResUserInfo.class);
//        lay anh avatar base 64 if (!CommonUtils.isNullOrEmpty(user.getAvatar())) {
//            responseData.setAvatar(s3ClientImp.getFileBase64(user.getAvatar()));
//        }
//        responseData.setUserName(usersEntity.getUserName());
//        logger.info("[{}] Response getUserProfile data = {}", clientMessageId, JsonUtil.toJson(responseData));
//        return responseData;
//    }
//
//    @Override
//    public ResUserLock lockAccount(ReqUserLock request) {        /* B3: Check thông tin đầu vào */
//        String clientMessageId = CommonUtils.getCurrentClientMessageId();
//        logger.info("[{}] Get accountLockReq data = {}", clientMessageId, JsonUtil.toJson(request));
//        if (request == null) {
//            throw new MBException(ErrConstans.INPUT_ERROR_001);
//        }        /* B4,B5: Check path và role */
//        logger.info("[{}] Start query tokenInfoEntity ", clientMessageId);
//        TokenInfoEntity tokenInfo = tokenInfoRepo.findFirstByUserIdOrderByCreateDateDesc(usersContext.getUserId());
//        logger.info("[{}] Query tokenInfoEntity end data = {}", clientMessageId, JsonUtil.toJson(tokenInfo));        /* B6: Check user tồn tại */        /* B7: Check trạng thái tk */
//        logger.info("[{}] Start query usersEntity ", clientMessageId);
//        UsersEntity usersEntity = usersRepo.findById(usersContext.getUserId()).orElseThrow(() -> new MBException(ErrConstans.USERID_ERROR_001.getSoaErrorCode(), ErrConstans.USERID_ERROR_001.getSoaErrorDesc()));
//        logger.info("[{}] Query usersEntity end data = {}", clientMessageId, JsonUtil.toJson(usersEntity));
//        if (Objects.equals(EActive.IN_ACTIVE.value, usersEntity.getIsActive())) {
//            throw new MBException(ErrConstans.LOGIN_ERROR_006);
//        }
//        logger.info("[{}] Star query loginHistoryEntity ", clientMessageId);
//        List<LoginHistoryEntity> loginHistoryEntities = loginHistoryRepo.findByUserNameAndTypeOrderByCreateDateDesc(usersEntity.getUserName(), ELoginHisType.LOCK_ACCOUNT.value);
//        logger.info("[{}] Query loginHistoryEntity end data = {}", clientMessageId, JsonUtil.toJson(loginHistoryEntities));
//        LoginHistoryEntity history = null;
//        if (!loginHistoryEntities.isEmpty()) {
//            LoginHistoryEntity hisTemp = loginHistoryEntities.getFirst();
//            if (Objects.equals(ELoginStatus.FAILED.value, hisTemp.getStatus())) {
//                history = hisTemp;
//            }
//        }        /* B8: Check password có khớp không */
//        if (!passwordEncoder.matches(request.getPassword(), usersEntity.getUserPass())) {
//            if (history == null) {
//                history = loginHistoryService.saveLoginHistory(usersEntity.getUserName(), 0L, ELoginStatus.FAILED.value, null, ELoginHisType.LOCK_ACCOUNT.value);
//            }
//            long loginFail = history.getLoginFail();
//            if (loginFail < 3) {
//                Tăng login_fail lên 1 history.setLoginFail(loginFail + 1);
//                logger.info("[{}] Start save loginHistoryEntity data = {}", clientMessageId, JsonUtil.toJson(history));
//                loginHistoryRepo.save(history);
//                logger.info("[{}] Save loginHistoryEntity end ", clientMessageId);
//                throw new MBException(ErrConstans.LOCKUSER_ERROR_001);
//            } else {
//                history.setLoginFail(EPasswordThreshold.RESET.value);
//                logger.info("[{}] Start save loginHistoryEntity data = {}", clientMessageId, JsonUtil.toJson(history));
//                loginHistoryRepo.save(history);
//                logger.info("[{}] Save loginHistoryEntity end ", clientMessageId);
//                logger.info("[{}] Start query tokenInfoEntity ", clientMessageId);
//                TokenInfoEntity tokenInfoEntity = tokenInfoRepo.findFirstByUserIdOrderByCreateDateDesc(usersEntity.getId());
//                logger.info("[{}] Query tokenInfoEntity end data = {}", clientMessageId, JsonUtil.toJson(tokenInfoEntity));
//                if (tokenInfoEntity != null) {
//                    tokenInfoEntity.setIsLogout(ELogoutStatus.LOGGED_OUT.value);
//                    logger.info("[{}] Start save tokenInfoEntity data = {}", clientMessageId, JsonUtil.toJson(tokenInfoEntity));
//                    tokenInfoRepo.save(tokenInfoEntity);
//                    logger.info("[{}] Save tokenInfoEntity end ", clientMessageId);
//                }
//                Cập nhật trạng thái thiết bị thành Đăng xuất if (tokenInfo != null) {
//                    DeviceLoginEntity deviceLogin = deviceLoginRepository.findByTokenInfoId(tokenInfo.getId());
//                    if (deviceLogin != null) {
//                        deviceLogin.setIsActive(EActive.IN_ACTIVE.value);
//                        logger.info("[{}] Start save deviceLoginEntity start data = {}", clientMessageId, JsonUtil.toJson(deviceLogin));
//                        deviceLoginRepository.save(deviceLogin);
//                        logger.info("[{}] Save deviceLoginEntity end ", clientMessageId);
//                    }
//                }
//                throw new MBException(ErrConstans.LOCKUSER_ERROR_002);
//            }
//        }        /* B9: Cập nhật trạng thái */ usersEntity.setIsActive(EActive.IN_ACTIVE.value);
//        logger.info("[{}] Start save usersEntity data = {}", clientMessageId, JsonUtil.toJson(usersEntity));
//        usersRepo.save(usersEntity);
//        logger.info("[{}] Save usersEntity end ", clientMessageId);
//        Danh sách device hoạt động if (tokenInfo != null) {
//            List<DeviceLoginEntity> activeDevice = deviceLoginRepository.findByTokenInfoIdAndIsActive(tokenInfo.getId(), EActive.ACTIVE.value);
//            if (!activeDevice.isEmpty()) {
//                for (DeviceLoginEntity deviceLoginEntity : activeDevice) {
//                    deviceLoginEntity.setIsActive(EActive.IN_ACTIVE.value);
//                }
//                logger.info("[{}] Start save all deviceLoginEntity data = {}", clientMessageId, JsonUtil.toJson(activeDevice));
//                deviceLoginRepository.saveAll(activeDevice);
//                logger.info("[{}] Save all deviceLoginEntity end ", clientMessageId);
//            }
//        }
//        Danh sách token hoạt động List<
//        TokenInfoEntity > activeTokens = tokenInfoRepo.findByUserIdAndIsLogout(usersEntity.getId(), 0L);
//        if (!activeTokens.isEmpty()) {
//            for (TokenInfoEntity tokenInfoEntity : activeTokens) {
//                tokenInfoEntity.setIsLogout(ELogoutStatus.LOGGED_OUT.value);
//            }
//            logger.info("[{}] Start save all tokenInfoEntity data = {}", clientMessageId, JsonUtil.toJson(activeTokens));
//            tokenInfoRepo.saveAll(activeTokens);
//            logger.info("[{}] Save all tokenInfoEntity end ", clientMessageId);
//        }
//        loginHistoryService.saveLoginHistory(usersEntity.getUserName(), 0L, ELoginStatus.SUCCESS.value, null, ELoginHisType.LOCK_ACCOUNT.value);
//        return ModelMapperUtils.toObject(usersEntity, ResUserLock.class);
//    }
//
//    @Override
//    public ResMailUpdate updateMail(ReqMailUpdate request) {        /* B4: Check thông tin đầu vào */
//        String clientMessageId = CommonUtils.getCurrentClientMessageId();
//        logger.info("[{}] Get updateMailReq data = {}", clientMessageId, JsonUtil.toJson(request));
//        logger.info("[{}] Start query usersEntity ", clientMessageId);
//        UsersEntity usersEntity = usersRepo.findByUserName(usersContext.getCurrentUserName());
//        logger.info("[{}] Start query usersEntity end data = {}", clientMessageId, JsonUtil.toJson(usersEntity));
//        Check user active checkUserIsActive (usersEntity);
//        if (Objects.equals(EActive.IN_ACTIVE.value, usersEntity.getIsActive())) {
//            throw new MBException(ErrConstans.LOGIN_ERROR_006);
//        }
//        logger.info("[{}] Start query userDetailEntity ", clientMessageId);
//        UserDetailEntity userDetail = userDetailRepository.findByUserId(usersEntity.getId());
//        logger.info("[{}] Query userDetailEntity end data = {}", clientMessageId, JsonUtil.toJson(userDetail));
//        User chưa có email
//        if (CommonUtils.isNullObject(userDetail.getEmailChange()) && CommonUtils.isNullObject(userDetail.getEmail())) {
//            userDetail.setEmailChange(request.getEmailChange());
//            logger.info("[{}] Start save userDetailEntity data = {}", clientMessageId, JsonUtil.toJson(userDetail));
//            userDetailRepository.save(userDetail);
//            logger.info("[{}] Save userDetailEntity end ", clientMessageId);
//            return ModelMapperUtils.toObject(userDetail, ResMailUpdate.class);
//        }        /* B9: Check email tồn tại*/
//        if (userDetailRepository.existsByEmail(request.getEmailChange())) {
//            throw new MBException(ErrConstans.UPDATE_EMAIL_ERROR_001);
//        }        /* B11: Check status email cũ đang gắn với tk */
//        Long verify = usersContext.getCurrentUser().getIsVerify();
//        if (verify == 0L) {            /* B12 & B13: Check merchant tồn tại và active */
//            CallBackRequest callBackRequest = new CallBackRequest();
//            callBackRequest.setClientMessageId(clientMessageId);
//            callBackRequest.setData(usersEntity.getMerchantId());
//            ResponseData<GetMerchantBasicInfoOutput> merchantMerchantInfo = merchantXMMCallBack.getMerchantBasicInfo(callBackRequest);
//            GetMerchantBasicInfoOutput getMerchantBasicInfoOutput = merchantMerchantInfo.getData();
//            if (getMerchantBasicInfoOutput == null || !Objects.equals(EMerchantStatus.ACTIVE.message, getMerchantBasicInfoOutput.getStatus()))
//                throw new MBException(ErrConstans.LOGIN_ERROR_009);
//        }
//        Update mail if (userDetail != null) {
//            userDetail.setEmailChange(request.getEmailChange());
//            logger.info("[{}] Start save userDetailEntity data = {}", clientMessageId, JsonUtil.toJson(userDetail));
//            userDetailRepository.save(userDetail);
//            logger.info("[{}] Save userDetailEntity end ", clientMessageId);
//        }        /**         * B14: Hệ thống MA-Service thực hiện cập nhật thông tin email         *       Gui link xac thuc tai khoan qua email cho ng dung         */        /**         *      Gui link xac thuc tai khoan qua email cho nguoi dung         */
//        logger.info("[{}] Start query emailTemplateEntity ", clientMessageId);
//        EmailTemplateEntity mailTemp = emailTemplateRepository.findByTemplateName(EAccount.VERIFY_ACCOUNT.value);
//        logger.info("[{}] Query emailTemplateEntity end data = {}", clientMessageId, JsonUtil.toJson(mailTemp));
//        List<String> params = new ArrayList<>();
//        params.add(userDetail.getFullName());
//        han xac thuc Date expVerify = DateTimeUtils.addDay(1);
//        logger.info("[{}] Expire date data = {}", clientMessageId, expVerify);
//        String token = EncryptionUtils.encrypt(String.valueOf(usersEntity.getId()), appProperties.encryptionKey);
//        logger.info("[{}] Verify token data = {}", clientMessageId, token);
//        params.add(token);
//        params.add(DateTimeUtils.convertDateToString(new Date(), "HH:mm:ss dd/MM/yyyy"));
//        String mailContent = MessageFormat.format(mailTemp.getContent(), params.toArray());
//        logger.info("[{}] Mail content data = {}", clientMessageId, mailContent);
//        sendmail ResponseData<
//        CreateEmailResponse > mailRes = sendMailService.sendMail(clientMessageId, request.getEmailChange(), mailTemp.getSubject(), mailContent, null);
//        logger.info("[{}] Send mail data = {}", clientMessageId, JsonUtil.toJson(mailRes));
//        SendMailResModel sendMailResModel = new SendMailResModel(null, new Date());
//        userDetail.setLinkVerify(token);
//        userDetail.setExpireVerify(expVerify);
//        logger.info("[{}] Start save userDetailEntity data = {}", clientMessageId, JsonUtil.toJson(userDetail));
//        userDetailRepository.save(userDetail);
//        logger.info("[{}] Save userDetailEntity end ", clientMessageId);
//        return ModelMapperUtils.toObject(userDetail, ResMailUpdate.class);
//    }
//
//    @Override
//    public Map<String, String> updateAvatar(MultipartFile request) {        /* B4: Check thông tin đầu vào */
//        String clientMessageId = CommonUtils.getCurrentClientMessageId();
//        logger.info("[{}] get updateAvatar data = {}", clientMessageId, JsonUtil.toJson(request));
//        if (request == null || request.isEmpty()) {
//            throw new MBException(ErrConstans.INPUT_ERROR_001);
//        }
//        check định dạng, kich thuoc file
//        CommonUtils.validateFileAvatar(request);        /* B7: Check user tồn tại */        /* B8: Check trạng thái tk */
//        logger.info("[{}] Start query usersEntity ", clientMessageId);
//        UsersEntity usersEntity = usersRepo.findByUserName(usersContext.getCurrentUserName());
//        logger.info("[{}] Query userEntity end data = {}", clientMessageId, JsonUtil.toJson(usersEntity));
//        checkUserIsActive(usersEntity);        /* B9: Kiểm tra mã độc */
//        MultipartFile avatarFile = request.getFile();
//        if (avatarFile == null || avatarFile.isEmpty()) {
//            throw new MBException(ErrConstans.USER_ERROR_001);
//        }        /* B10: Lưu ảnh lên S3 Private */
//        String uuid = s3ClientImp.uploadFile(request);        /* B11: Lưu đường dẫn ảnh */
//        logger.info("[{}] Start query userDetailEntity ", clientMessageId);
//        UserDetailEntity dataSave = userDetailRepository.findByUserId(usersEntity.getId());
//        logger.info("[{}] Query userDetailEntity end data = {}", clientMessageId, JsonUtil.toJson(dataSave));
//        dataSave.setAvatar(uuid);
//        logger.info("[{}] Start save userDetailEntity data = {}", clientMessageId, JsonUtil.toJson(dataSave));
//        userDetailRepository.save(dataSave);
//        logger.info("[{}] Save userDetailEntity end ", clientMessageId);
//        Wrap result in ResponseData object Map<String, String > response = new HashMap<>();
//        response.put("uuid", uuid);
//        return response;
//        Return the ResponseData object
//    }
//
//    @Override
//    public Map<String, Long> verification(String verifyKey) {        /**         * B9: Validate input         * B10: Compare user id from access token with user id in verify key         * B11: Check link expire time         *  case 1: valid -> move to step 12         *  case 2: invalid -> thr ex         * B12: Check user verification status         *  case 1: unverified or update new email         *  -> check if this email is verified by another user         *  -> else verify user         *  case 2: verified         *  -> thr ex         */
//        String clientMessageId = CommonUtils.getCurrentClientMessageId();
//        logger.info("[{}] Get verifyKey data = {}", clientMessageId, verifyKey);
//        Map<String, Long> rs = new HashMap<>();
//        if (!StringBaseUtils.inputValid(verifyKey, null)) {
//            throw new MBException(ErrConstans.INPUT_ERROR_001);
//        }
//        Long userId = usersContext.getCurrentUser().getId();
//        logger.info("[{}] userId from token data = {}", clientMessageId, userId);
//        logger.info("[{}] Start query userDetailEntity ", clientMessageId);
//        UserDetailEntity userDetailEntity = userDetailRepository.findByUserId(userId);
//        logger.info("[{}] Query userDetailEntity end data = {}", clientMessageId, JsonUtil.toJson(userDetailEntity));
//        if (!Objects.equals(userDetailEntity.getLinkVerify(), verifyKey)) {
//            if (!StringBaseUtils.isNullOrEmpty(userDetailEntity.getEmail()) && StringBaseUtils.isNullOrEmpty(userDetailEntity.getEmailChange()) && Objects.equals(EVerify.VERIFIED_USER.value, userDetailEntity.getIsVerify())) {
//                throw new MBException(ErrConstans.AUTH_ERROR_004);
//            } else {
//                throw new MBException(ErrConstans.INPUT_ERROR_001);
//            }
//        }
//        logger.info("[{}] Decrypt verifyKey data = {}", clientMessageId, verifyKey);
//        String decryptedKey = EncryptionUtils.decrypt(verifyKey, appProperties.encryptionKey);
//        String[] keyParts = decryptedKey.split("\\|");
//        Long userIdFromKey = Long.parseLong(keyParts[0]);
//        logger.info("[{}] userId from key = {}", clientMessageId, userIdFromKey);
//        if (!Objects.equals(userId, userIdFromKey)) {
//            throw new MBException(ErrConstans.AUTH_ERROR_006);
//        }
//        long expDate = DateTimeUtils.differenDate(new Date(), userDetailEntity.getExpireVerify());
//        logger.info("[{}] Check expire verify link data = {}", clientMessageId, expDate);
//        if (expDate <= 0) {
//            userDetailEntity.setEmailChange(null);
//            userDetailRepository.save(userDetailEntity);
//            throw new MBException(ErrConstans.AUTH_ERROR_005);
//        }
//        boolean emailVerified = userDetailRepository.existsByEmailAndUserIdNot(userDetailEntity.getEmailChange(), userId);
//        logger.info("[{}] Check valid email data = {}", clientMessageId, emailVerified);
//        if (emailVerified) {
//            throw new MBException(ErrConstans.AUTH_ERROR_007);
//        }
//        userDetailEntity.setEmail(userDetailEntity.getEmailChange());
//        userDetailEntity.setEmailChange(null);
//        userDetailEntity.setLinkVerify(null);
//        userDetailEntity.setExpireVerify(null);
//        userDetailEntity.setIsVerify(EVerify.VERIFIED_USER.value);
//        logger.info("[{}] Start save userDetailEntity data = {}", clientMessageId, JsonUtil.toJson(userDetailEntity));
//        userDetailRepository.save(userDetailEntity);
//        logger.info("[{}] Save userDetailEntity end ", clientMessageId);
//        rs.put("status", EVerify.VERIFIED_USER.value);
//        return rs;
//    }
//
//    @Override
//    public Map<String, String> sendVerificationMail() {        /**         * B1: Check path role and path description         * B2: Check exists user by user id         * B3: Check account active status         * B4: Check user verification status         * B5: Check exists merchant and merchant active status         * B6: Check if user has been sent verification link or not         *  case 1: does not exist link, generate a new one         *  case 2: exists link, check expire time -> send back to user         */
//        String clientMessageId = CommonUtils.getCurrentClientMessageId();
//        Long id = usersContext.getUserId();
//        logger.info("[{}] Start query usersEntity ", clientMessageId);
//        UsersEntity usersEntity = usersRepo.findById(id).orElseThrow(() -> new MBException(ErrConstans.USER_ERROR_001));
//        logger.info("[{}] Query usersEntity end data = {}", clientMessageId, JsonUtil.toJson(usersEntity));
//        checkUserIsActive(usersEntity);
//        logger.info("[{}] Start query userDetailEntity ", clientMessageId);
//        UserDetailEntity userDetail = userDetailRepository.findByUserId(id);
//        logger.info("[{}] Query userDetailEntity end data: {}", clientMessageId, JsonUtil.toJson(userDetail));
//        if (StringBaseUtils.isNullOrEmpty(userDetail.getEmail()) && StringBaseUtils.isNullOrEmpty(userDetail.getEmailChange())) {
//            throw new MBException(ErrConstans.AUTH_ERROR_001);
//        }
//        if (!StringBaseUtils.isNullOrEmpty(userDetail.getEmail()) && StringBaseUtils.isNullOrEmpty(userDetail.getEmailChange())) {
//            throw new MBException(ErrConstans.AUTH_ERROR_002);
//        }
//        MM CALLBACK CallBackRequest callBackRequest = new CallBackRequest();
//        callBackRequest.setClientMessageId(clientMessageId);
//        callBackRequest.setData(usersEntity.getMerchantId());
//        ResponseData<GetMerchantBasicInfoOutput> merchantMerchantInfo = merchantXMMCallBack.getMerchantBasicInfo(callBackRequest);
//        GetMerchantBasicInfoOutput getMerchantBasicInfoOutput = merchantMerchantInfo.getData();
//        if (getMerchantBasicInfoOutput == null || !Objects.equals(EMerchantStatus.ACTIVE.message, getMerchantBasicInfoOutput.getStatus()))
//            throw new MBException(ErrConstans.LOGIN_ERROR_009);        /** kiểm tra email_change của user này có trùng với email của user nào khác không?         * tránh trường hợp 2 user cùng xác thực 1 email         */
//        logger.info("[{}] Check valid email ", clientMessageId);
//        boolean emailAlreadyVerified = userDetailRepository.existsByEmailAndUserIdNot(userDetail.getEmailChange(), usersEntity.getId());
//        logger.info("[{}] Valid email data = {}", clientMessageId, emailAlreadyVerified);
//        if (emailAlreadyVerified) {
//            throw new MBException(ErrConstans.AUTH_ERROR_007);
//        }
//        logger.info("[{}] Start query emailTemplateEntity ", clientMessageId);
//        EmailTemplateEntity templateEntity = emailTemplateRepository.findByTemplateName(EAccount.VERIFY_ACCOUNT.value);
//        logger.info("[{}] Query emailTemplateEntity end data: {}", clientMessageId, JsonUtil.toJson(templateEntity));
//        boolean isExpired = (userDetail.getExpireVerify() == null || userDetail.getExpireVerify().before(new Date()));
//        logger.info("[{}] Check link expire data = {}", clientMessageId, isExpired);
//        SendMailResModel model = sendMailWithTemplate(userDetail, templateEntity, isExpired);
//        userDetail.setLinkVerify(model.getUrl());
//        userDetail.setExpireVerify(model.getExpireVerify());
//        logger.info("[{}] Start save userDetailEntity data = {}", clientMessageId, JsonUtil.toJson(userDetail));
//        userDetailRepository.save(userDetail);
//        logger.info("[{}] Save userDetailEntity end ", clientMessageId);
//        gửi mail thành công return 1 Map<String, String> result = new HashMap<>();
//        result.put("status", String.valueOf(1L));
//        result.put("verifyKey", userDetail.getLinkVerify());
//        return result;
//    }
//
//    private SendMailResModel sendMailWithTemplate(UserDetailEntity userDetail, EmailTemplateEntity mailTemp, boolean isExpired) {
//        String clientMessageId = CommonUtils.getCurrentClientMessageId();
//        List<String> params = new ArrayList<>();
//        Date expireVerify = isExpired ? DateTimeUtils.addDay(1) : userDetail.getExpireVerify();
//        logger.info("[{}] Expire date data = {}", clientMessageId, expireVerify);
//        String rawData = userDetail.getUserId() + "|" + expireVerify.getTime();
//        String verifyKey = isExpired ? EncryptionUtils.encrypt(rawData, appProperties.encryptionKey) : userDetail.getLinkVerify();
//        logger.info("[{}] Verify key data = {}", clientMessageId, verifyKey);
//        params.add(userDetail.getFullName());
//        params.add(verifyKey);
//        params.add(DateTimeUtils.convertDateToString(new Date(), "HH:mm:ss dd/MM/yyyy"));
//        String mailContent = MessageFormat.format(mailTemp.getContent(), params.toArray());
//        logger.info("[{}] Mail content data = {}", clientMessageId, mailContent);
//        ResponseData<CreateEmailResponse> res = sendMailService.sendMail(clientMessageId, userDetail.getEmailChange(), mailTemp.getSubject(), mailContent, null);
//        logger.info("[{}] Send mail data = {}", clientMessageId, JsonUtil.toJson(res));
//        SendMailResModel sendMailResModel = new SendMailResModel(null, new Date());
//        sendMailResModel.setUrl(verifyKey);
//        sendMailResModel.setExpireVerify(expireVerify);
//        logger.info("[{}] Send mail res data = {}", clientMessageId, JsonUtil.toJson(sendMailResModel));
//        return sendMailResModel;
//    }
//
//    private UsersEntity findUserById(Long userId) {
//        return usersRepo.findById(userId).orElseThrow(() -> new MBException(ErrConstans.USERID_ERROR_001));
//    }
//
//    private void checkUserIsActive(UsersEntity usersEntity) {
//        if (usersEntity != null && Objects.equals(EActive.IN_ACTIVE.value, usersEntity.getIsActive())) {
//            throw new MBException(ErrConstans.LOGIN_ERROR_006);
//        }
//    }
//
//    public void insertDeviceLoginEntity(UserInfoModel userInfoModel, TokenInfoEntity tokenInfoSave, ServiceHeader serviceHeader) {
//        DeviceLoginEntity dataSave = new DeviceLoginEntity();
//        dataSave.setUserId(userInfoModel.getId());
//        dataSave.setDeviceId(serviceHeader.getDeviceId());
//        dataSave.setDeviceName(serviceHeader.getSourceAppName());
//        dataSave.setDeviceToken(serviceHeader.getSourceAppToken());
//        dataSave.setOsType(1L);
//        dataSave.setIsActive(EActive.ACTIVE.value);
//        dataSave.setCreateDate(new Date());
//        deviceLoginRepository.save(dataSave);
//    }
//}