package com.jeestudio.datasource.service.gen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.jeestudio.common.entity.common.persistence.Page;
import com.jeestudio.common.entity.gen.GenDevelopUser;
import com.jeestudio.datasource.mapper.base.gen.GenDevelopUserDao;
import com.jeestudio.datasource.service.common.CrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: GenDevelopUser Service
 * @author: David
 * @Date: 2020-02-11
 */
@Service
public class GenDevelopUserService extends CrudService<GenDevelopUserDao, GenDevelopUser> {

	@Autowired
	private GenDevelopUserDao genDevelopUserDao;

	public GenDevelopUser get(String id) {
		return super.get(id);
	}

	public List<GenDevelopUser> findList(GenDevelopUser genDevelopUser) {
		return super.findList(genDevelopUser);
	}

	public Page<GenDevelopUser> findPage(Page<GenDevelopUser> page, GenDevelopUser genDevelopUser) {
		return super.findPage(page, genDevelopUser);
	}

	@Transactional(readOnly = false)
	public void save(GenDevelopUser genDevelopUser) {
		super.save(genDevelopUser);
	}

	@Transactional(readOnly = false)
	public void saveV(GenDevelopUser genDevelopUser) {
		super.saveV(genDevelopUser);
	}

	@Transactional(readOnly = false)
	public void delete(GenDevelopUser genDevelopUser) {
		super.delete(genDevelopUser);
	}

	public GenDevelopUser getGenUserByEmail(String email) {
		GenDevelopUser genDevelopUser = genDevelopUserDao.getGenUserByEmail(email);
		return genDevelopUser;
	}

	public Boolean isOK(String email, String mac) {
		Boolean isOk = false;
		StringBuffer strBuf = new StringBuffer();
		GenDevelopUser genDevelopUser = genDevelopUserDao.getGenUserByEmail(email);

		if (genDevelopUser.getOkNum() == 99) return true;

		Date date = new Date();
		Date okDate = genDevelopUser.getOkDate();
		if (false == okDate.before(date)) {
			if (genDevelopUser.getOkNum() > 0) {
				if (genDevelopUser.getMac1() == "") {
					isOk = true;
					SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = simple.format(date.getTime());
					String log = mac + "_" + time + "_" + isOk + "\r\n";
					strBuf.append(log);
					strBuf.append(genDevelopUser.getVisitLog() != null ? genDevelopUser.getVisitLog() : "");
					String visitLog = strBuf.toString();
					genDevelopUserDao.updateMacByEmail(email, mac, visitLog);
				} else {
					String[] macNumber = getMacNumber(genDevelopUser.toString());
					int k = 0;
					for (int i = 0; i < genDevelopUser.getOkNum(); i++) {
						if (macNumber[i] != null) {
							if (macNumber[i].equals(mac)) {
								isOk = true;
								SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String time = simple.format(date.getTime());
								String log = mac + "_" + time + "_" + isOk + "\r\n";
								strBuf.append(log);
								strBuf.append(genDevelopUser.getVisitLog() != null ? genDevelopUser.getVisitLog() : "");
								String visitLog = strBuf.toString();
								genDevelopUserDao.updateVisitLogByEmail(email, visitLog);
								break;
							} else {
								k = i;
							}
						} else {
							k = i;
							break;
						}
					}
					if (false == isOk) {
						int m = k + 1;
						if (m < genDevelopUser.getOkNum()) {
							isOk = true;
							SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = simple.format(date.getTime());
							String log = mac + "_" + time + "_" + isOk + "\r\n";
							strBuf.append(log);
							strBuf.append(genDevelopUser.getVisitLog() != null ? genDevelopUser.getVisitLog() : "");
							String visitLog = strBuf.toString();
							genDevelopUserDao.updateMaciByEmail(email, mac, visitLog, m);
						}
					}
				}
			}
		}
		return isOk;
	}

	private String[] getMacNumber(String macs) {
		String[] macNum = macs.split(",");
		String[] macNums = new String[macNum.length];
		for (int i = 0; i < macNum.length; i++) {
			String[] num = macNum[i].split("=");
			if (num.length > 1) {
				macNums[i] = macNum[i].split("=")[1];
			}
		}
		return macNums;
	}
}