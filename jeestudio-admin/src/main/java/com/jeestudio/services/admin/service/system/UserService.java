package com.jeestudio.services.admin.service.system;

import com.jeestudio.common.entity.common.Zform;
import com.jeestudio.common.entity.system.User;
import com.jeestudio.services.admin.dao.datasourceFeign.DatasourceFeign;
import com.jeestudio.utils.DateUtil;
import com.jeestudio.utils.ResultJson;
import com.jeestudio.utils.StringUtil;
import com.jeestudio.utils.ValidatePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description: SecLog Service
 * @author: David
 * @Date: 2020-06-23
 */
@Service
public class UserService {

    @Autowired
    DatasourceFeign datasourceFeign;

    @Value(value = "${sec.switch}")
    private boolean secSwitch;

    public ResultJson getCurrentUserView(String currentUserId){
        return datasourceFeign.getCurrentUserView(currentUserId);
    }

    public ResultJson save(Zform zform, String loginName) throws Exception {
        boolean isValid = true;
        String password = zform.getS03();
        if(this.secSwitch && StringUtil.isNotEmpty(password)) {
            isValid = this.validPassword(password);
            if (isValid) {
                Date theDate = DateUtil.addDays(new Date(), 7);
                zform.setD01(theDate);
                zform.setS11("0");
            }
        }
        if (false == isValid) {
            ResultJson resultJson = new ResultJson();
            resultJson.setCode(ResultJson.CODE_FAILED);
            if(password.length() < 10) {
                resultJson.setMsg("密码至少10位字符");
                resultJson.setMsg_en("At least 10 characters are required.");
            } else {
                resultJson.setMsg("密码格式要求：大写字母，小写字母，数字和特殊符号，至少三种");
                resultJson.setMsg_en("Passwords must use at least three of the four available character types: uppercase letters, lowercase letters, numbers, and symbols.");
            }
            return resultJson;
        } else {
            return datasourceFeign.saveUser(zform, loginName);
        }
    }

    public Integer getLoginExceptionCount(String loginName){
        Integer count = datasourceFeign.getLoginExceptionCount(loginName);
        return count;
    }

    public void clearLoginExceptionCount(String loginName){
        datasourceFeign.clearLoginExceptionCount(loginName);
    }

    public Boolean isPasswordExpired(String loginName){
        Boolean expired = datasourceFeign.isPasswordExpired(loginName);
        return expired;
    }

    public void unlockUser(String loginName){
        datasourceFeign.unlockUser(loginName);
    }

    public void lockUser(String loginName){
        datasourceFeign.lockUser(loginName);
    }

    public void addLoginExceptionCount(String loginName) {
        datasourceFeign.addLoginExceptionCount(loginName);
    }

    public ResultJson changePassword(String oldPassword, String newPassword, String loginName) throws Exception {
        ResultJson resultJson = new ResultJson();
        User user = datasourceFeign.getUserByLoginName(loginName);
        if (false == ValidatePassword.validateUserPassword(oldPassword, user.getPassword())){
            resultJson.setCode(ResultJson.CODE_FAILED);
            resultJson.setMsg("原密码错误");
            resultJson.setMsg_en("The old password you have provided is wrong");
        } else {
            boolean isValid = this.validPassword(newPassword);
            if (false == isValid) {
                resultJson.setCode(ResultJson.CODE_FAILED);
                if(newPassword.length() < 10) {
                    resultJson.setMsg("密码至少10位字符");
                    resultJson.setMsg_en("At least 10 characters are required.");
                } else {
                    resultJson.setMsg("密码格式要求：大写字母，小写字母，数字和特殊符号，至少三种");
                    resultJson.setMsg_en("Passwords must use at least three of the four available character types: uppercase letters, lowercase letters, numbers, and symbols.");
                }
                return resultJson;
            } else {
                datasourceFeign.changePassword(newPassword, loginName);
                resultJson.setCode(ResultJson.CODE_SUCCESS);
                resultJson.setMsg("修改密码成功");
                resultJson.setMsg_en("Password was updated successfully");
            }
        }
        return resultJson;
    }

    private boolean validPassword(String password) {
        boolean isValid = true;
        if(this.secSwitch && StringUtil.isNotEmpty(password)) {
            if (password.length() < 10) {
                isValid = false;
            } else {
                int number = 0;
                int smallWord = 0;
                int word = 0;
                int special = 0;
                String numberReg = "[0-9]+";
                String smallWordReg = "[a-z]+";
                String wordReg = "[A-Z]+";
                String specialReg = "[~`!@#$%^&*()_\\-+={\\[}\\]|\\\\:;\"'<,>.?/]+";
                char[] charArray = password.toCharArray();
                for (char c : charArray) {
                    String index = String.valueOf(c);
                    if (index.matches(numberReg)) {
                        number++;
                    }
                    if (index.matches(smallWordReg)) {
                        smallWord++;
                    }
                    if (index.matches(wordReg)) {
                        word++;
                    }
                    if (index.matches(specialReg)) {
                        special++;
                    }
                }

                int total = 0;
                if (word != 0) {
                    total++;
                }
                if (smallWord != 0) {
                    total++;
                }
                if (number != 0) {
                    total++;
                }
                if (special != 0) {
                    total++;
                }
                if (total < 3) {
                    isValid = false;
                }
            }
        }
        return isValid;
    }
}
