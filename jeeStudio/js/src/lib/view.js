/**

 @Name：layuiAdmin 视图模块
 @Author：贤心
 @Site：http://www.layui.com/admin/
 @License：LPPL
    
 */

layui.define(['laytpl', 'layer','form'], function (exports) {
    var LAY_$ = layui.jquery
        , laytpl = layui.laytpl
        , layer = layui.layer
        , setter = layui.setter
        , device = layui.device()
        , hint = layui.hint()

        //对外接口
        , view = function (id) {
            return new Class(id);
        }

        , SHOW = 'layui-show', LAY_BODY = 'LAY_app_body'

        //构造器
        , Class = function (id) {
            this.id = id;
            this.container = LAY_$('#' + (id || LAY_BODY));
        };




    //加载中
    view.loading = function (elem) {
    	$("body").append(
    		 this.elemLoad = LAY_$('<div class="loading-task"></div><div class="loading-div"><i class="layui-anim layui-anim-rotate layui-anim-loop layui-icon layui-icon-loading layadmin-loading"></i></div>')
    	);
       	/*elem.append(
            this.elemLoad = LAY_$('<i class="layui-anim layui-anim-rotate layui-anim-loop layui-icon layui-icon-loading layadmin-loading"></i>')
        );*/
    };

    //移除加载
    view.removeLoad = function () {
        this.elemLoad && this.elemLoad.remove();
    };

    //清除 token，并跳转到登入页
    view.exit = function () {
        //清空本地记录的 token
        layui.data(setter.tableName, {
            key: setter.request.tokenName
            , remove: true
        });

        //跳转到登入页
        //location.hash = '/admin/login';
        //wy修改
        //location.href = '/views/index.html#/admin/login';
        location.href = 'index.html#/admin/login';
    };

    //Ajax请求
    view.req = function (options) {
        var that = this
            , success = options.success
            , error = options.error
            , request = setter.request
            , response = setter.response
            , debug = function () {
                return setter.debug
                    ? '<br><cite>URL：</cite>' + options.url
                    : '';
            }
            , loading = options.loading || false
            , tipOk = options.tipOk||false;

        options.data = options.data || {};
        /*options.headers = options.headers || {};

        if (request.tokenName) {
            var sendData = typeof options.data === 'string'
                ? JSON.parse(options.data)
                : options.data;

            //自动给参数传入默认 token
            options.data[request.tokenName] = request.tokenName in sendData
                ? options.data[request.tokenName]
                : (layui.data(setter.tableName)[request.tokenName] || '');

            //自动给 Request Headers 传入 token
            options.headers[request.tokenName] = request.tokenName in options.headers
                ? options.headers[request.tokenName]
                : (layui.data(setter.tableName)[request.tokenName] || '');
        }*/
       //wy添加token
        var $token = localStorage.getItem('$tokenBPM') || "";
        var headers = {};
        if($token){
        	headers.token = $token;
        }
        
		if(options.url.indexOf("?") > -1){
   			options.url += "&_v="+new Date().getTime();
		}else{
   			options.url += "?_v="+new Date().getTime();
  		}

        delete options.success;
        delete options.error;
        delete options.loading;
        delete options.tipOk;
        
        //wy修改
//      if (loading && options.type==='post')
		if (loading){
			view.loading(LAY_$('#' + LAY_BODY));
		}

        return $.ajax($.extend({
            type: 'get'
            , dataType: 'json'
            , headers: headers
            , success: function (res) {
            	//wy修改 保存token
                if(res.token){
                	localStorage.setItem("$tokenBPM",res.token);
                }
                
                var statusCode = response.statusCode;

                //只有 response 的 code 一切正常才执行 done
                if (res[response.statusName] === statusCode.ok) {
                    if (tipOk)
                        view.ok([
                            '<cite></cite> ' + (layui.admin.getCodeMsg(options.url, res[response.statusName]) || (layui.admin.lang() ? 'Successful operation.' : '操作成功。'))
                            , debug()
                        ].join(''), { title: layui.admin.lang() ? 'Tips' : '提示' });
					
					//wy修改
					if(options.showMsg){
						view.ok([
                            '<cite></cite> ' + (layui.admin.lang() ? res.msg_en : res.msg)
                            , debug()
                        ].join(''), { title: layui.admin.lang() ? 'Tips' : '提示' });
					}
					
                    typeof options.done === 'function' && options.done(res);
                }

                //登录状态失效，清除本地 access_token，并强制跳转到登入页
                else if (res[response.statusName] === statusCode.logout) {
                    view.exit();
                }

                else if (res[response.statusName] === statusCode.error) {
                	if(typeof options.errorFuc === 'function'){
                		options.errorFuc(res);
                	}
                	
                	//wy修改
                	view.error([
                        '<cite></cite> ' + (layui.admin.lang() ? res.msg_en : res.msg)
                        , debug()
                    ].join(''), { title: layui.admin.lang() ? 'Error' : '错误' })
                	
                    /*view.error([
                        '<cite></cite> ' + (layui.admin.lang() ? 'Server error.' : '服务器错误。')
                        , debug()
                    ].join(''), { title: layui.admin.lang() ? 'Error' : '错误' });*/
                }

                //其它异常
                else {
                    view.warn([
                        '<cite></cite> ' + (layui.admin.getCodeMsg(options.url, res[response.statusName]) || (layui.admin.lang() ? 'Return status code exception.' : '返回状态码异常。'))
                        , debug()
                    ].join(''), { title: layui.admin.lang() ? 'Wran' : '警告' });
                }

 				//wy修改
//      		if (loading && options.type==='post')
                if (loading)
                    view.removeLoad();

                //只要 http 状态码正常，无论 response 的 code 是否正常都执行 success
                typeof success === 'function' && success(res);
            }
            , error: function (e, code) {
            	if(options.shade) {
			      	layer.closeAll()
			    }
            	
                var statusCode = response.statusCode;
                if(e && e.responseJSON){
                	if (e.responseJSON[response.statusName] === statusCode.logout) {
	                    view.removeLoad();
	                    view.exit();
	                }
                }else{
                	view.error([
	                    layui.admin.lang() ? 'Request exception, please try again.<br><cite>error message：</cite>' : '请求异常，请重试。<br><cite>错误信息：</cite>' + code
	                    , debug()
	                ].join(''), { title: layui.admin.lang() ? 'Error' : '错误' });
	
	 				//wy修改
	//      		if (loading && options.type==='post')
	                if (loading)
	                    view.removeLoad();
	
	                typeof error === 'function' && error(res);
                }
            }
        }, options));
    };

    //弹窗
    view.popup = function (options) {
        var success = options.success
            , skin = options.skin;

        delete options.success;
        delete options.skin;

        return layer.open(LAY_$.extend({
            type: 1
            , title: layui.admin.lang() ? 'Tips' : '提示'
            , content: ''
            , id: 'LAY-system-view-popup'
            , skin: 'layui-layer-admin' + (skin ? ' ' + skin : '')
            , shadeClose: true
            , closeBtn: false
            , success: function (layero, index) {
                var elemClose = LAY_$('<i class="layui-icon" close>&#x1006;</i>');
                layero.append(elemClose);
                elemClose.on('click', function () {
                    layer.close(index);
                });
                typeof success === 'function' && success.apply(this, arguments);
            }
            , end: function () {
                typeof end === 'function' && end.apply(this, arguments);
            }
        }, options));
    };

    //异常提示
    view.error = function (content, options) {
        return view.popup(LAY_$.extend({
            content: content
            , maxWidth: 400
            //,shade: 0.01
            , offset: '60px'
            , anim: 6
            , id: 'LAY_adminError'
        }, options));
    };

    //异常提示
    view.warn = function (content, options) {
        return view.popup(LAY_$.extend({
            content: content
            , maxWidth: 400
            , offset: '60px'
            , anim: 2
            , shade: 0
            , time: 2000
            , id: 'LAY_adminWarn'
        }, options));
    };

    //异常提示
    view.ok = function (content, options) {
         return view.popup(LAY_$.extend({
            content: content
            , maxWidth: 300
            , offset: '60px'
            , anim: 1
            , shade: 0
            , time: 1000
            , id: 'LAY_adminOk'
        }, options));
    };

    //异常提示
    view.confirm = function (content, options) {
        content = '<cite></cite> '+(content || layui.admin.lang() ? 'Do you perform operations?' : '是否执行？');
        return view.popup(LAY_$.extend({
            content: content
            , maxWidth: 300
            , offset: '300px'
            , btn: ['Yes', 'No']
            , yes: function (index, layero) {
                layer.close(index);
                typeof options.doyes === 'function' && options.doyes.apply(this, arguments);
            }, btn2: function (index, layero) {
                layer.close(index);
            }
            , anim: 6
            , shade: 0
            , id: 'LAY_adminConfirm'
        }, options));
    };


    //请求模板文件渲染
    Class.prototype.render = function (views, params) {
        var that = this, router = layui.router();
        views = setter.views + views + setter.engine;

        LAY_$('#' + LAY_BODY).children('.layadmin-loading').remove();
        view.loading(that.container); //loading

        //请求模板
        LAY_$.ajax({
            url: views
            , type: 'get'
            , dataType: 'html'
            , data: {
                v: layui.cache.version
            }
            , success: function (html) {
                html = '<div>' + html + '</div>';

                var elemTitle = LAY_$(html).find('title')
                    , title = elemTitle.text() || (html.match(/\<title\>([\s\S]*)\<\/title>/) || [])[1];

                var res = {
                    title: title
                    , body: html
                };

                elemTitle.remove();
                that.params = params || {}; //获取参数

                if (that.then) {
                    that.then(res);
                    delete that.then;
                }

                //.parse方法把html代码封装进入that对象
                that.parse(html);
                view.removeLoad();

                if (that.done) {
                    that.done(res);
                    delete that.done;
                }

            }
            , error: function (e) {
                view.removeLoad();

                if (that.render.isError) {
                    return view.error('请求视图文件异常，状态：' + e.status);
                };

                if (e.status === 404) {
                    that.render('errorPage/404');
                } else {
                    that.render('errorPage/error');
                }

                that.render.isError = true;
            }
        });
        return that;
    };

    //解析模板
    Class.prototype.parse = function (html, refresh, callback) {
        var that = this
            , isScriptTpl = typeof html === 'object' //是否模板元素
            , elem = isScriptTpl ? html : LAY_$(html)
            , elemTemp = isScriptTpl ? html : elem.find('*[template]')
            , fn = function (options) {
                var tpl = laytpl(options.dataElem.html())
                    , res = LAY_$.extend({
                        params: router.params
                    }, options.res);

                options.dataElem.after(tpl.render(res));
                typeof callback === 'function' && callback();

                try {
                    options.done && new Function('d', options.done)(res);
                } catch (e) {
                    console.error(options.dataElem[0], '\n存在错误回调脚本\n\n', e)
                }
            }
            , router = layui.router();

        elem.find('title').remove();
        that.container[refresh ? 'after' : 'html'](elem.children());

        router.params = that.params || {};

        //遍历模板区块
        for (var i = elemTemp.length; i > 0; i--) {
            (function () {
                var dataElem = elemTemp.eq(i - 1)
                    , layDone = dataElem.attr('lay-done') || dataElem.attr('lay-then') //获取回调
                    , url = laytpl(dataElem.attr('lay-url') || '').render(router) //接口 url
                    , data = laytpl(dataElem.attr('lay-data') || '').render(router) //接口参数
                    , headers = laytpl(dataElem.attr('lay-headers') || '').render(router); //接口请求的头信息

                try {
                    data = new Function('return ' + data + ';')();
                } catch (e) {
                    hint.error('lay-data: ' + e.message);
                    data = {};
                };

                try {
                    headers = new Function('return ' + headers + ';')();
                } catch (e) {
                    hint.error('lay-headers: ' + e.message);
                    headers = headers || {}
                };

                if (url) {
                    view.req({
                        type: dataElem.attr('lay-type') || 'get'
                        , url: url
                        , data: data
                        , dataType: 'json'
                        , headers: headers
                        , success: function (res) {
                            fn({
                                dataElem: dataElem
                                , res: res
                                , done: layDone
                            });
                        }
                    });
                } else {
                    fn({
                        dataElem: dataElem
                        , done: layDone
                    });
                }
            }());
        }

        return that;
    };

    //直接渲染字符
    Class.prototype.send = function (views, data) {
        var tpl = laytpl(views || this.container.html()).render(data || {});
        this.container.html(tpl);
        return this;
    };

    //局部刷新模板
    Class.prototype.refresh = function (callback) {
        var that = this
            , next = that.container.next()
            , templateid = next.attr('lay-templateid');

        if (that.id != templateid) return that;

        that.parse(that.container, 'refresh', function () {
            that.container.siblings('[lay-templateid="' + that.id + '"]:last').remove();
            typeof callback === 'function' && callback();
        });

        return that;
    };

    //视图请求成功后的回调
    Class.prototype.then = function (callback) {
        this.then = callback;
        return this;
    };

    //视图渲染完毕后的回调
    Class.prototype.done = function (callback) {
        this.done = callback;
        return this;
    };

    //对外接口
    exports('view', view);
});