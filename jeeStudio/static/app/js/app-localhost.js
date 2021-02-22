/**
 * 演示程序当前的 “注册/登录” 等操作，是基于 “本地存储” 完成的
 * 当您要参考这个演示程序进行相关 app 的开发时，
 * 请注意将相关方法调整成 “基于服务端Service” 的实现。
 **/
(function($, owner) {
	/**
	 * 新用户注册
	 **/
	owner.reg = function(regInfo, callback) {
		callback = callback || $.noop;
		regInfo = regInfo || {};
		regInfo.username = regInfo.username || '';
		regInfo.password = regInfo.password || '';
		if (regInfo.username.length < 5) {
			return callback('用户名最短需要 5 个字符');
		}
		if (regInfo.password.length < 6) {
			return callback('密码最短需要 6 个字符');
		}
		if (!checkEmail(regInfo.email)) {
			return callback('邮箱地址不合法');
		}
		var users = JSON.parse(localStorage.getItem('$users') || '[]');
		users.push(regInfo);
		localStorage.setItem('$users', JSON.stringify(users));
		return callback();
	};

	var checkEmail = function(email) {
		email = email || '';
		return (email.length > 3 && email.indexOf('@') > -1);
	};

	/**
	 * 找回密码
	 **/
	owner.forgetPassword = function(email, callback) {
		callback = callback || $.noop;
		if (!checkEmail(email)) {
			return callback('邮箱地址不合法');
		}
		return callback(null, '新的随机密码已经发送到您的邮箱，请查收邮件。');
	};

	/**
	 * 获取应用本地配置
	 **/
	owner.setSettings = function(settings) {
		settings = settings || {};
		localStorage.setItem('$settings', JSON.stringify(settings));
	}

	/**
	 * 设置应用本地配置
	 **/
	owner.getSettings = function() {
		var settingsText = localStorage.getItem('$settings') || "{}";
		return JSON.parse(settingsText);
	}
	
	/**
		 * 获取本地是否安装客户端
		 **/
	owner.isInstalled = function(id) {
		if (id === 'qihoo' && mui.os.plus) {
			return true;
		}
		if (mui.os.android) {
			var main = plus.android.runtimeMainActivity();
			var packageManager = main.getPackageManager();
			var PackageManager = plus.android.importClass(packageManager)
			var packageName = {
				"qq": "com.tencent.mobileqq",
				"weixin": "com.tencent.mm",
				"sinaweibo": "com.sina.weibo"
			}
			try {
				return packageManager.getPackageInfo(packageName[id], PackageManager.GET_ACTIVITIES);
			} catch (e) {}
		} else {
			switch (id) {
				case "qq":
					var TencentOAuth = plus.ios.import("TencentOAuth");
					return TencentOAuth.iphoneQQInstalled();
				case "weixin":
					var WXApi = plus.ios.import("WXApi");
					return WXApi.isWXAppInstalled()
				case "sinaweibo":
					var SinaAPI = plus.ios.import("WeiboSDK");
					return SinaAPI.isWeiboAppInstalled()
				default:
					break;
			}
		}
	}
	
	/*
	 * 封装ajax，传递token
	 */
	owner.ip = 'http://192.168.90.207:8330/';
	owner.path = 'http://192.168.90.207:8330/gtoa/a/';
	owner.ajax = function(url, type, data, successCallback, isAsync, failedCallback, errorCallback){
		var asy = true;
		if(isAsync != undefined || isAsync != null) {
			asy = isAsync;
		}
		
		var $token = owner.getToken();
		var headers = {};//{'Content-Type':'application/json'}
		
		if (url != "login") {
			if($token.temp_token != "undefined"){
				headers.temp_token = $token.temp_token;
			}
			if($token.long_token != "undefined"){
				headers.long_token = $token.long_token;
			}
		}
		mui.ajax(owner.path + url,{
			data:data,
			async:asy,
			dataType:'json',//服务器返回json格式数据
			type:type,//HTTP请求类型
			timeout:10000,//超时时间设置为10秒；
			headers:headers,	              
			success:function(result){
				//获取返回的headers里的token，如果存在则存在token里
				var temp_token = result.temp_token;
				var long_token = result.long_token;
				
				var $token = {};
				if(temp_token){
					$token.temp_token = temp_token;
				}
				if(long_token){
					$token.long_token = long_token;
				}
				owner.setToken($token);
				
				if(result.status) {
					if(successCallback) {
						returnData = successCallback(result);
					}
				}
				else {
					if(failedCallback) {
						failedCallback(result);
					}else{
						mui.toast(result.msg);
						setTimeout(function() {
							$.openWindow({
								id: 'login',
								url: '/login.html',
								show: {
									aniShow: 'pop-in'
								},
								waiting: {
									autoShow: false
								},
								styles: {
									cachemode:"noCache"
								}
							});
						}, 1000);
					}
				}
			},
			error:function(xhr,type,errorThrown){
				mui.toast('请求失败');
				if(errorCallback) {
					errorCallback();
				}
			}
		});
	}
	
	/**
	 * 获取当前token
	 **/
	owner.getToken = function() {
		var stateText = localStorage.getItem('$token') || "{}";
		return JSON.parse(stateText);
	};
	/**
	 * 设置当前token
	 **/
	owner.setToken = function(token) {
		token = token || {};
		localStorage.setItem('$token', JSON.stringify(token));
		//var settings = owner.getSettings();
		//settings.gestures = '';
		//owner.setSettings(settings);
	};
	
	/*
	 * 创建用户名缓存
	 */
	owner.createState = function(name) {
		var state = owner.getState();
		state.username = name;
		owner.setState(state);
	};
	/**
	 * 获取当前状态
	 **/
	owner.getState = function() {
		var stateText = localStorage.getItem('$state') || "{}";
		return JSON.parse(stateText);
	};
	/**
	 * 设置当前状态
	 **/
	owner.setState = function(state) {
		state = state || {};
		localStorage.setItem('$state', JSON.stringify(state));
		//var settings = owner.getSettings();
		//settings.gestures = '';
		//owner.setSettings(settings);
	};
	
	/*获取链接参数*/
	owner.getAllUrlParam = function(w) {
		w = w ? w : window;
	   	var url = decodeURI(w.location.search); //获取url中"?"符后的字串
	   	var theRequest = new Object();
	   	if (url.indexOf("?") != -1) {
	      	var str = url.substr(1);
	      	var strs = str.split("&");
	      	for(var i = 0; i < strs.length; i ++) {
	         	theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
	      	}
	   	}
	   	return theRequest;
	};
	
	//封装jquery $.grep方法
	owner.grep = function(elems, callback, inv) {
		var retVal, ret = [], i = 0, length = elems.length;
		inv = !!inv;//转成布尔型
		for (; i < length; i++) {
			retVal = !!callback(elems[i], i); // 注意callback的参数是先value后key
			if (inv !== retVal)
				ret.push(elems[i]);
	    }
		return ret;
	};
	
	//根据按钮对应的类型判断是否需要查询一个用户列表
	owner.isNeedUserList = function(type){
		var typeArr = ['saveAndStart','saveAndComplete'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	};
	owner.isNeedNodeList = function(type){
		var typeArr = ['saveAndSuperReject'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	};
	owner.isTerminate = function(type){
		var typeArr = ['saveAndTerminate'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	};
	
}(mui, window.app = {}));

window.paceOptions = {
	ajax: true,
	eventLag: false,
	elements: false
}　　