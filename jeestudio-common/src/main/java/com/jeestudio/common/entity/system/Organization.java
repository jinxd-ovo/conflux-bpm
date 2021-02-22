package com.jeestudio.common.entity.system;

import com.jeestudio.common.entity.common.DataEntity;

/**
 * @Description: Organization
 * @author: houxl
 * @Date: 2019-01-11
 */
public class Organization extends DataEntity<Organization> {

    private static final long serialVersionUID = 1L;

    private String ownerCode;
    private String orgNumber;
    private String orgName;
    private User primaryPerson;
    private String orgEffect;
    private Integer orgSequenceNumber;

    public Organization() {
        super();
    }

    public Organization(String id){
        super(id);
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getOrgNumber() {
        return orgNumber;
    }

    public void setOrgNumber(String orgNumber) {
        this.orgNumber = orgNumber;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public User getPrimaryPerson() {
        return primaryPerson;
    }

    public void setPrimaryPerson(User primaryPerson) {
        this.primaryPerson = primaryPerson;
    }

    public String getOrgEffect() {
        return orgEffect;
    }

    public void setOrgEffect(String orgEffect) {
        this.orgEffect = orgEffect;
    }

    public Integer getOrgSequenceNumber() {
        return orgSequenceNumber;
    }

    public void setOrgSequenceNumber(Integer orgSequenceNumber) {
        this.orgSequenceNumber = orgSequenceNumber;
    }
}
