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
	
	/**
	 * 获取Ip地址
	 **/
	owner.getIp = function() {
		var serverText = localStorage.getItem('$ip') || "";
		return serverText;
	};
	/*
	 * 设置Ip地址
	 */
	owner.setIp = function(server) {
		server = server || "";
		localStorage.setItem('$ip', server);
	};
	
	/**
	 * 获取端口
	 **/
	owner.getPort = function() {
		var serverText = localStorage.getItem('$port') || "";
		if(serverText){
			return ":" + serverText;
		}
		return serverText;
	};
	/*
	 * 设置端口
	 */
	owner.setPort = function(server) {
		server = server || "";
		localStorage.setItem('$port', server);
	};
	
	/**
	 * 获取项目名称
	 **/
	owner.getProject = function() {
		var serverText = localStorage.getItem('$project') || "";
		if(serverText){
			return serverText;
		}
		return "gtoa";
	};
	/*
	 * 设置项目名称
	 */
	owner.setProject = function(server) {
		server = server || "";
		localStorage.setItem('$project', server);
	};
	
	/**
	 * 获取Path地址
	 **/
	owner.getPath = function() {
		var serverText = localStorage.getItem('$path') || "";
		return serverText;
	};
	/*
	 * 设置Path地址
	 */
	owner.setPath = function(server) {
		server = server || "";
		localStorage.setItem('$path', server);
	};
	/**
	 * 获取Path地址
	 **/
	owner.getStaticPath = function() {
		var serverText = localStorage.getItem('$staticPath') || "";
		return serverText;
	};
	/*
	 * 设置Path地址
	 */
	owner.setStaticPath = function(server) {
		server = server || "";
		localStorage.setItem('$staticPath', server);
	};
	
	owner.getLocalStorage = function($key) {
		var stateText = localStorage.getItem("bpmApp"+$key);
		if(stateText){
			return JSON.parse(stateText);
		}else{
			return "";
		}
	};
	owner.setLocalStorage = function($key,json) {
		if(json){
			localStorage.setItem("bpmApp"+$key, JSON.stringify(json));
		}else{
			localStorage.setItem("bpmApp"+$key, "");
		}
	};
	
	/**
	 * 获取当前token
	 **/
	owner.getToken = function() {
		return localStorage.getItem('$tokenBPMApp');
	};
	/**
	 * 设置当前token
	 **/
	owner.setToken = function(token) {
		localStorage.setItem('$tokenBPMApp', token);
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
	}
	
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
	owner.isRollBack = function(type){
		var typeArr = ['saveAndReject'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	}
	
	owner.openWindow = function(url) {
		if(url.indexOf("?") > -1){
			url += "&v="+new Date().getTime();
		}else{
			url += "?v="+new Date().getTime();
		}
		mui.openWindow({
			id: new Date().getTime(),
//			url: owner.getpath() + url,
			url: owner.host + url,
			show: {
				aniShow: 'pop-in'
			},
			waiting: {
				autoShow: true
			},
			styles: {
				cachemode: "noCache"
			}
		});
	};
	
	
	/*
	 * 封装ajax，传递token
	 */
	owner.ip = 'http://' + owner.getIp() + '/';
	owner.path = 'http://' + owner.getIp() + owner.getPort() + '/' + owner.getProject() +'/gtoa/a';
	owner.staticPath = 'http://' + owner.getLocalStorage("staticPath") + '/Views/app/';
	owner.host = "";
	
	owner.ajax = function(options){
		var success = options.success;
		delete options.success;
		var failed = options.failed;
		delete options.failed;
		var errorCallback = options.error;
		delete options.error;

		if(options.shade) {
			mui.showLoading("loading..", "div");
		}

		var opt = {
			type: 'get',
			contentType: "application/json",
			dataType: "json",
			processData:true
		};

		var opt = mui.extend(true, opt, options);

		var headers = {};
		var $token = localStorage.getItem('$tokenBPMApp') || "";
		if($token) {
			headers.token = $token;
		}
		if(opt.contentType){
			headers['Content-Type'] = opt.contentType
		}

		opt.headers = headers;

		opt.success = function(res) {
			if(options.shade) {
				mui.hideLoading();
			}
			if(res.code == 0 && typeof success == "function") {
				if(res.token) {
					localStorage.setItem("$tokenBPMApp", res.token);
				}
				if(typeof success == "function") {
					success(res);
				}
			} else {
				if(typeof failed == "function") {
					failed(res);
				} else {
					mui.toast(res.msg);
				}
			}
		};
		opt.error = function(e, code) {
			console.log(options.url,code)
			if(options.shade) {
				mui.hideLoading();
			}
			/*var statusCode = response.statusCode;*/
			if(e && e.response) {
				var res = JSON.parse(e.response);
				if(res.code === 1001) {
					mui.toast("token失效");
					owner.openWindow('../../../../jeeStudioApp/login.html');
					/* var data = {
						username: "admin",
						password: "admin"
					};
					owner.ajax({
						url: owner.path + '/login',
						type: 'post',
						data: data,
						contentType: "application/x-www-form-urlencoded",
						shade: true,
						success: function(){
							location.reload();
						}
					}); */
					
				}
			}
			if(errorCallback) {
				errorCallback();
			}
		}
		mui.ajax(opt);
	}
	
}(mui, window.app = {}));

window.paceOptions = {
	ajax: true,
	eventLag: false,
	elements: false
}　　

;(function($, window) {
    //显示加载框  
    $.showLoading = function(message,type) {  
        if ($.os.plus && type !== 'div') {  
            $.plusReady(function() {  
                plus.nativeUI.showWaiting(message);  
            });  
        } else {  
            var html = '';  
            html += '<i class="mui-spinner mui-spinner-white"></i>';  
            html += '<p class="text">' + (message || "数据加载中") + '</p>';  

            //遮罩层  
            var mask=document.getElementsByClassName("mui-show-loading-mask");  
            if(mask.length==0){  
                mask = document.createElement('div');  
                mask.classList.add("mui-show-loading-mask");  
                document.body.appendChild(mask);  
                mask.addEventListener("touchmove", function(e){e.stopPropagation();e.preventDefault();});  
            }else{  
                mask[0].classList.remove("mui-show-loading-mask-hidden");  
            }  
            //加载框  
            var toast=document.getElementsByClassName("mui-show-loading");  
            if(toast.length==0){  
                toast = document.createElement('div');  
                toast.classList.add("mui-show-loading");  
                toast.classList.add('loading-visible');  
                document.body.appendChild(toast);  
                toast.innerHTML = html;  
                toast.addEventListener("touchmove", function(e){e.stopPropagation();e.preventDefault();});  
            }else{  
                toast[0].innerHTML = html;  
                toast[0].classList.add("loading-visible");  
            }  
        }     
    };  

    //隐藏加载框  
      $.hideLoading = function(callback) {  
        if ($.os.plus) {  
            $.plusReady(function() {  
                plus.nativeUI.closeWaiting();  
            });  
        }   
        var mask=document.getElementsByClassName("mui-show-loading-mask");  
        var toast=document.getElementsByClassName("mui-show-loading");  
        if(mask.length>0){  
            mask[0].classList.add("mui-show-loading-mask-hidden");  
        }  
        if(toast.length>0){  
            toast[0].classList.remove("loading-visible");  
            callback && callback();  
        }  
      }  
})(mui, window);  