/**

 @Name：layuiAdmin 核心模块
 @Author：贤心
 @Site：http://www.layui.com/admin/
 @License：LPPL
    
 */

layui.define('view', function (exports) {
    var $ = layui.jquery
        , laytpl = layui.laytpl
        , element = layui.element
        , setter = layui.setter
        , view = layui.view
        , device = layui.device()

        , $win = $(window), $body = $('body')
        , container = $('#' + setter.container)

        , SHOW = 'layui-show', HIDE = 'layui-hide', THIS = 'layui-this', DISABLED = 'layui-disabled', TEMP = 'template'
        , APP_BODY = '#LAY_app_body', APP_FLEXIBLE = 'LAY_app_flexible'
        , FILTER_TAB_TBAS = 'layadmin-layout-tabs'
        , APP_SPREAD_SM = 'layadmin-side-spread-sm', TABS_BODY = 'layadmin-tabsbody-item'
        , ICON_SHRINK = 'layui-icon-shrink-right', ICON_SPREAD = 'layui-icon-spread-left'
        , SIDE_SHRINK = 'layadmin-side-shrink', SIDE_MENU = 'LAY-system-side-menu'

        //通用方法
        , admin = {
            //wy添加公共接口地址 
            /*host: 'http://localhost:8900',
		    basePath:'http://127.0.0.1:8020/jeeStudio/json',
            dictionaryUrl: '/getDictionarySelect.json',*/
            host: 'http://127.0.0.1:8020/jeeStudio',
            basePath: 'http://localhost:8900/jeeStudio/gtoa/a',
            formPath: 'http://localhost:8910',
            dictionaryUrl: '/sys/dict/getDictListView',
			
            //v: '1.2.1 pro'
            v: layui.cache.version

            ,lang: function () {
                return layui.data(layui.setter.tableName).language.lang || '';
            }
            ,skin:  function () {
                return layui.data(layui.setter.tableName).skin || '';
            }
            //数据的异步请求
            , req: view.req
  
            //清除本地 token，并跳转到登入页
            , exit: view.exit

            //xss 转义
            , escape: function (html) {
                return String(html || '').replace(/&(?!#?[a-zA-Z0-9]+;)/g, '&amp;')
                    .replace(/</g, '&lt;').replace(/>/g, '&gt;')
                    .replace(/'/g, '&#39;').replace(/"/g, '&quot;');
            }
            , decimal:function(num, v) {
                var vv = Math.pow(10, v);
                return admin.formatNum(Math.round(num * vv) / vv);
            }
            , formatNum: function (strNum) {
                return (strNum+'').replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,');
            }
    
            //格式化
            , timeFormat_YMD: function (v) {
                return admin.getYMDHMS(v).substr(0, 10);
            }
            , timeFormat_YMDH: function (v) {
                return admin.getYMDHMS(v).substr(0, 13);
            }
            , timeFormat_YMDHM: function (v) {
                return admin.getYMDHMS(v).substr(0, 16);
            }
            , timeFormat_YMDHMS: function (v) {
                return admin.getYMDHMS(v).substr(0, 19);
            }
            , getExcelDate: function (datetime) {
                if (!isNaN(datetime)) {
                    let time = new Date((datetime - 1) * 24 * 60 * 60 * 1000);

                    time.setYear(time.getFullYear() - 70);
                    time.setHours(time.getHours() - 8);
                    return R.getYMDHMS(time);
                } else
                    return datetime;
            }
            , getYMDHMS: function (time) {
                if (!time) return '';
                var date='';

                if (!/Invalid|NaN/.test(new Date(time))) {
                    date = new Date(time);
                } else if (time.indexOf('/Date') > -1) {

                    time = time.substr(1, time.length - 2);
                    var obj = eval('(' + "{Date: new " + time + "}" + ')');
                    date = obj["Date"];
                }
                
                var year = date.getFullYear();
                var month = date.getMonth() + 1;
                var day = date.getDate();
                month = month < 10 ? '0' + month : month;
                day = day < 10 ? '0' + day : day;

                var YMD = year + '-' + month + '-' + day;

                var hours = date.getHours();
                var minutes = date.getMinutes();
                var seconds = date.getSeconds();

                hours = hours < 10 ? '0' + hours : hours;
                minutes = minutes < 10 ? '0' + minutes : minutes;
                seconds = seconds < 10 ? '0' + seconds : seconds;

                var HMS = hours + ':' + minutes + ':' + seconds;
                //+ '.' + date.getMilliseconds()

                return YMD + ' ' + HMS;
            }

            , jsonClone: function (data) {
                return JSON.parse(JSON.stringify(data));
            }
            , sessionData: function (options) {
                var local = layui.sessionData(setter.tableName);

                local.dataset = local.dataset || {};

                layui.each(options, function (key, value) {
                    local.dataset[key] = value;
                });

                layui.sessionData(setter.tableName, {
                    key: 'dataset'
                    , value: local.dataset
                });
            }
            , removeSessionData: function (key) {
                var local = layui.sessionData(setter.tableName);

                local.dataset = local.dataset || {};

                local.dataset[key] = null;

                layui.sessionData(setter.tableName, {
                    key: 'dataset'
                    , value: local.dataset
                });
            }
            ,clearSessionData: function() {
                    layui.sessionData(setter.tableName, {
                        key: 'dataset'
                        , value: null
                    });
                }

            , getSessionList: function (list) {
                var local = layui.sessionData(setter.tableName);

                local.dataset = local.dataset || {};

                return local.dataset[list];
            }
            , initSessionData: function () {
                admin.req({
                	//wy修改 page-form-field,page-table-cell,
                   	 url: admin.basePath + admin.dictionaryUrl
                    , data: {
                    	types: 'data-params,response-code,custom-params'
                    }
                    , loading: false
                    , tipOk: false
                    , success: function (res) {
                        if (res.data.data && res.data.data.length > 0) {
                            var dataSet = admin.filterList(res.data.data, { parentType: ['page-form-field', 'page-table-cell', 'data-params', 'response-code','custom-params'] });
                            if (dataSet.length > 0){
                            	$.each(dataSet, function (idx, item) {
                                    var list = admin.filterList(res.data.data, { type: item.type, parentType: item.member });
                                    if (list.length > 0) {
                                        var setTable = {};
                                        setTable[item.type + '-' + item.member] = list;
                                        admin.sessionData(setTable);
                                    }
                                });
                            }
                        }
                    }
                });
            }
            , filterList: function (jsonList, where) {
                var tempArray = [];

                var index = 0;
                $.each(where, function (k, v) {
                    index++;
                    if ($.isArray(v))
                        $.each(v, function (ak, av) {
                            $.each(jsonList, function (idx, json) {
                                if (json[k] === av) {
                                    tempArray.push(json);
                                }
                            });
                        });
                    else
                        $.each(jsonList, function (idx, json) {
                            if (json[k] === v) {
                                tempArray.push(json);
                            }
                        });
                        
                    if (tempArray.length === 0)
                        return false;

                    if (index < Object.keys(where).length) {
                        if (tempArray.length > 0) {
                            jsonList = JSON.parse(JSON.stringify(tempArray));
                            tempArray = [];
                        }
                    }
                });
                return tempArray;
            }

            , getTabTitle: function (url) {
                var obj = admin.filterList(admin.getSessionList('page'), { pageUrl: url })[0];
                return obj?obj['pageName' + admin.lang()]:'';
            }

            , getTableTitle: function (table) {
                var obj = admin.filterList(admin.getSessionList('page'), { sign: table })[0];
                return obj ? obj['pageName' + admin.lang()] : '';
            }

            , getFormTitle: function (table, btn,type) {
                var formTitle = [];
                var tableObj = admin.filterList(admin.getSessionList('page'), { sign: table })[0];
                if (tableObj) {
                    formTitle.push(tableObj['pageName' + admin.lang()]);
                    var btnObj = admin.filterList(admin.getSessionList('page'), { parentID: tableObj.pageID, sign: btn, type: type })[0];
                    if (btnObj)
                        formTitle.push(btnObj['pageName' + admin.lang()]);
                }
                return formTitle.join(' - ');
            }

            , getToolFormTitle: function (table, btn) {
                return admin.getFormTitle(table, btn,3);
            }
            , getToolbarFormTitle: function (table, btn) {
                return admin.getFormTitle(table, btn, 4);
            }
            , getPageToolBtn: function (sign, where) {
                var page = admin.getSessionList('page');
                var item = admin.filterList(page, { sign: sign })[0];
               
                var html = '';
                if (item)
                    $.each(admin.filterList(page, $.extend(where, { parentID: item.pageID, type: 3 })), function (idx, data) {
                        //+ (data.pageIcon ? '<i class="layui-icon layuiadmin-button-btn ' + data.PageIcon + '"></i>':'')
                        html += '<button class="layui-btn layuiadmin-btn-admin' + (data.sign === 'batchdel' ? ' layui-btn-danger' : ' layui-btn-default') + '" type="button" lay-event="' + data.sign + '">' +data['pageName' + admin.lang()] + '</button> ';
                    });
                return html;
            }
            , getPageToolbarBtn: function (sign, where) {
                var page = admin.getSessionList('page');
                var item = admin.filterList(page, { sign: sign })[0];
                var html = '';
                if (item)
                    $.each(admin.filterList(page, $.extend(where, { parentID: item.pageID, type: 4 })), function (idx, data) {
                        html += '<button class="layui-btn layui-btn-xs' + (data.sign === 'del' ? ' layui-btn-danger' : ' layui-btn-default') + '" lay-event="' + data.sign + '">' + (data.pageIcon ? '<i class="layui-icon ' + data.PageIcon+'"></i>' : data['pageName' + admin.lang()]) + '</button> ';
                    });
                return html;
            }

            , getCodeMsg: function (url, code) {
                var field = admin.filterList(admin.getSessionList('response-code-' + url), { member: code + '' })[0];
                return field ? field['memberName' + admin.lang()] : null;
            }

            , getDictionary: function (type, table, call) {
                var list = admin.getSessionList(type + '-' + table);
                if (list) {
                    return typeof call === 'function' && call(admin.jsonClone(list));
                } else {
                	if(table){
                		admin.req({
		                	//wy修改
		                    /*url: '/Admin/getDictionarySelect'
		                    , type: 'post'*/
		                   	 url: admin.basePath + admin.dictionaryUrl
		                    , data: { 
		                    	types: table
		                    }
		                    , loading: false
		                    , tipOk: false
		                    , async: false
		                    , success: function (res) {
		                        if (res.data.data && res.data.data.length > 0) {
		                            var setTable = {};
		                            setTable[type + '-' + table] = res.data.data;
		                            admin.sessionData(setTable);
		                            return typeof call === 'function' && call(admin.jsonClone(res.data.data));
		                        }else{
		                        	//LayerUtil.warning("没有找到字典");
		                        }
		                    }
		                });
                	}
                }
            }
            , getDataParams: function (table, call) {
                admin.getDictionary('data-params', table, call);
            }
            , getDataParamsMember: function (table, key) {
                var item = admin.filterList(admin.getSessionList('data-params-' + table), { member: key + '' })[0];
                return item ? item['memberName' + R.lang()] : key;
            }
            , updateDataParams: function (table) {
            	if(table){
            		admin.req({
	                    //url: '/Admin/getDictionarySelect'
	                    url: admin.basePath + admin.dictionaryUrl
	                    , type: 'post'
	                    , data: { 
	                    	type: table
	                    }
	                    , loading: false
	                    , tipOk: false
	                    , success: function (res) {
	                        if (res.data.dict && res.data.dict.length > 0) {
	                            var setTable = {};
	                            setTable['data-params-' + table] = res.data.data;
	                            admin.sessionData(setTable);
	                        }
	                    }
	                });
            	}
            }
            , getCustomParams: function (table, call) {
                admin.getDictionary('custom-params', table, call);
            }
            , getCustomParamsMember: function (table, key) {
                var item = admin.filterList(admin.getSessionList('custom-params-' + table), { member: key + '' })[0];
                return item ? item['memberName' + R.lang()] : key;
            }
            , setFormSelectTitleLang: function (formid, filedName) {
                return admin.getDictionary('page-form-field', formid, function (d) {
                    var field = admin.filterList(admin.getSessionList('page-form-field-' + formid), { member: filedName })[0];
                    return field ? field['memberName' + admin.lang()] : '';
                });
            }
            , setFormSelectLang: function (formid, filedName) {
                return admin.getDictionary('page-form-field', formid, function (d) {
                    var field = admin.filterList(d, { member: filedName })[0];
                    return field ? (admin.lang() ? 'Please select ' : '请选择') + field['memberName' + admin.lang()] : '';
                });
            }

            //事件监听
            , on: function (events, callback) {
                return layui.onevent.call(this, setter.MOD_NAME, events, callback);
            }

            //弹出面板
            , popup: view.popup

            //右侧面板
            , popupRight: function (options) {
                //layer.close(admin.popup.index);
                return admin.popup.index = layer.open($.extend({
                    type: 1
                    , id: 'LAY_adminPopupR'
                    , anim: -1
                    , title: false
                    , closeBtn: false
                    , offset: 'r'
                    , shade: 0.1
                    , shadeClose: true
                    , skin: 'layui-anim layui-anim-rl layui-layer-adminRight'
                    , area: '300px'
                }, options));
            }

            //屏幕类型
            , screen: function () {
                var width = $win.width();
                if (width > 1200) {
                    return 3; //大屏幕
                } else if (width > 992) {
                    return 2; //中屏幕
                } else if (width > 768) {
                    return 1; //小屏幕
                } else {
                    return 0; //超小屏幕
                }
            }

            //侧边伸缩
            , sideFlexible: function (status) {
                var app = container
                    , iconElem = $('#' + APP_FLEXIBLE)
                    , screen = admin.screen();

                //设置状态，PC：默认展开、移动：默认收缩
                if (status === 'spread') {
                    //切换到展开状态的 icon，箭头：←
                    iconElem.removeClass(ICON_SPREAD).addClass(ICON_SHRINK);

                    //移动：从左到右位移；PC：清除多余选择器恢复默认
                    if (screen < 2) {
                        app.addClass(APP_SPREAD_SM);
                    } else {
                        app.removeClass(APP_SPREAD_SM);
                    }

                    app.removeClass(SIDE_SHRINK)
                } else {
                    //切换到搜索状态的 icon，箭头：→
                    iconElem.removeClass(ICON_SHRINK).addClass(ICON_SPREAD);

                    //移动：清除多余选择器恢复默认；PC：从右往左收缩
                    if (screen < 2) {
                        app.removeClass(SIDE_SHRINK);
                    } else {
                        app.addClass(SIDE_SHRINK);
                    }

                    app.removeClass(APP_SPREAD_SM)
                }

                layui.event.call(this, setter.MOD_NAME, 'side({*})', {
                    status: status
                });
            }

            //重置主体区域表格尺寸
            , resizeTable: function (delay) {
                var that = this, runResizeTable = function () {
                    that.tabsBody(admin.tabsPage.index).find('.layui-table-view').each(function () {
                        var tableID = $(this).attr('lay-id');
                        layui.table.resize(tableID);
                    });
                };
                if (!layui.table) return;
                delay ? setTimeout(runResizeTable, delay) : runResizeTable();
            }

            //主题设置
            , theme: function (options) {
                var theme = setter.theme
                    , local = layui.data(setter.tableName)
                    , id = 'LAY_layadmin_theme'
                    , style = document.createElement('style')
                    , styleText = laytpl([
                        //主题色
                        '.layui-side-menu,'
                        , '.layadmin-pagetabs .layui-tab-title li:after,'
                        , '.layadmin-pagetabs .layui-tab-title li.layui-this:after,'
                        , '.layui-layer-admin .layui-layer-title,'
                        , '.layadmin-side-shrink .layui-side-menu .layui-nav>.layui-nav-item>.layui-nav-child'
                        , '{background-color:{{d.color.main}} !important;}'

                        , '.layui-btn-default,.layui-layer-admin .layui-layer-title,.layui-layer-title{color: d.color.selected;background-color:{{d.color.header}} !important;}'

                        //选中色
                        , '.layui-nav-tree .layui-this,'
                        , '.layui-nav-tree .layui-this>a,'
                        , '.layui-nav-tree .layui-nav-child dd.layui-this,'
                        , '.layui-nav-tree .layui-nav-child dd.layui-this a'
                        , '{background-color:{{d.color.selected}} !important;}'

                        //logo
                        , '.layui-layout-admin .layui-logo{background-color:{{d.color.logo || d.color.main}} !important;}'

                        //头部色
                        , '{{# if(d.color.header){ }}'
                        , '.layui-layout-admin .layui-header{background-color:{{ d.color.header }};}'
                        , '.layui-layout-admin .layui-header a,'
                        , '.layui-layout-admin .layui-header a cite{color: #f8f8f8;}'
                        , '.layui-layout-admin .layui-header a:hover{color: #fff;}'
                        , '.layui-layout-admin .layui-header .layui-nav .layui-nav-more{border-top-color: #fbfbfb;}'
                        , '.layui-layout-admin .layui-header .layui-nav .layui-nav-mored{border-color: transparent; border-bottom-color: #fbfbfb;}'
                        , '.layui-layout-admin .layui-header .layui-nav .layui-this:after, .layui-layout-admin .layui-header .layui-nav-bar{background-color: #fff; background-color: rgba(255,255,255,.5);}'
                        , '.layadmin-pagetabs .layui-tab-title li:after{display: none;}'
                        , '{{# } }}'
                    ].join('')).render(options = $.extend({}, local.theme, options))
                    , styleElem = document.getElementById(id);

                //添加主题样式
                if ('styleSheet' in style) {
                    style.setAttribute('type', 'text/css');
                    style.styleSheet.cssText = styleText;
                } else {
                    style.innerHTML = styleText;
                }
                style.id = id;

                styleElem && $body[0].removeChild(styleElem);
                $body[0].appendChild(style);
                $body.attr('layadmin-themealias', options.color.alias);

                //本地存储记录
                local.theme = local.theme || {};
                layui.each(options, function (key, value) {
                    local.theme[key] = value;
                });
                layui.data(setter.tableName, {
                    key: 'theme'
                    , value: local.theme
                });
            }

            //初始化主题
            , initTheme: function (index) {
                var theme = setter.theme;
                index = index || 0;
                if (theme.color[index]) {
                    theme.color[index].index = index;
                    admin.theme({
                        color: theme.color[index]
                    });
                }
            }

            //语言设置
            , language: function (options) {
                var local = layui.data(setter.tableName);

                local.language = local.language || {};

                layui.each(options, function (key, value) {
                    local.language[key] = value;
                });
                

                layui.data(setter.tableName, {
                    key: 'language'
                    , value: local.language
                });
            }

            //初始化主题
            , initLanguage: function (lang) {
                admin.language({
                    lang: lang
                    , v: admin.v
                });
            }

            //记录最近一次点击的页面标签数据
            , tabsPage: {}

            //获取标签页的头元素
            , tabsHeader: function (index) {
                return $('#LAY_app_tabsheader').children('li').eq(index || 0);
            }

            //获取页面标签主体元素
            , tabsBody: function (index) {
                return $(APP_BODY).find('.' + TABS_BODY).eq(index || 0);
            }

            //切换页面标签主体
            , tabsBodyChange: function (index) {
                admin.tabsHeader(index).attr('lay-attr', layui.router().href);
                admin.tabsBody(index).addClass(SHOW).siblings().removeClass(SHOW);
                events.rollPage('auto', index);
            }

            , tabsMenuChange: function () {
                //$('#LAY-system-side-menu .layui-this').closest('.layui-nav-item').addClass('layui-nav-itemed');
                //$('#LAY-system-side-menu .layui-this').closest('dl.layui-nav-child').addClass('layui-nav-itemed');
            }

            //resize事件管理
            , resize: function (fn) {
                var router = layui.router()
                    , key = router.path.join('-');

                if (admin.resizeFn[key]) {
                    $win.off('resize', admin.resizeFn[key]);
                    delete admin.resizeFn[key];
                }

                if (fn === 'off') return; //如果是清除 resize 事件，则终止往下执行

                fn(), admin.resizeFn[key] = fn;
                $win.on('resize', admin.resizeFn[key]);
            }
            , resizeFn: {}
            , runResize: function () {
                var router = layui.router()
                    , key = router.path.join('-');
                admin.resizeFn[key] && admin.resizeFn[key]();
            }
            , delResize: function () {
                this.resize('off');
            }

            //关闭当前 pageTabs
            , closeThisTabs: function () {
                if (!admin.tabsPage.index) return;
                $(TABS_HEADER).eq(admin.tabsPage.index).find('.layui-tab-close').trigger('click');
            }

            //全屏
            , fullScreen: function () {
                var ele = document.documentElement
                    , reqFullScreen = ele.requestFullScreen || ele.webkitRequestFullScreen
                        || ele.mozRequestFullScreen || ele.msRequestFullscreen;
                if (typeof reqFullScreen !== 'undefined' && reqFullScreen) {
                    reqFullScreen.call(ele);
                }
            }

            //退出全屏
            , exitScreen: function () {
                var ele = document.documentElement;
                if (document.exitFullscreen) {
                    document.exitFullscreen();
                } else if (document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if (document.webkitCancelFullScreen) {
                    document.webkitCancelFullScreen();
                } else if (document.msExitFullscreen) {
                    document.msExitFullscreen();
                }
            }

            //纠正单页路由格式
            , correctRouter: function (href) {
            	//wy修改
                if (!/^\//.test(href)) href = '/' + href;

                //纠正首尾
                return href.replace(/^(\/+)/, '/')
                    .replace(new RegExp('\/' + setter.entry + '$'), '/'); //过滤路由最后的默认视图文件名（如：index）
            }

            //……

        };

    //事件
    var events = admin.events = {
    	//退出
    	logout: function () {
	        //执行退出接口
	        admin.req({
	        	//wy修改
	//          url: '/Account/SignOut'
	// 			, type: 'post'
	//			url: admin.basePath + '/logout.json'
				url: admin.basePath + '/logout'
	            , data: {}
	            , done: function (res) { //这里要说明一下：done 是只有 response 的 code 正常才会执行。而 succese 则是只要 http 为 200 就会执行
	
	                //清空本地记录的 token，并跳转到登入页
	                admin.exit();
	            }
	        });
	    },

		//伸缩
        flexible: function (othis) {
            var iconElem = othis.find('#' + APP_FLEXIBLE)
                , isSpread = iconElem.hasClass(ICON_SPREAD);
            admin.sideFlexible(isSpread ? 'spread' : null); //控制伸缩
            admin.resizeTable(350);
        }

        //刷新
        , refresh: function () {
            layui.index.render();
        }

        //输入框搜索
        , serach: function (othis) {
            othis.off('keypress').on('keypress', function (e) {
                if (!this.value.replace(/\s/g, '')) return;
                //回车跳转
                if (e.keyCode === 13) {
                    var href = othis.attr('lay-action')
                        , text = othis.attr('lay-text') || '搜索';

                    href = href + this.value;
                    text = text + ' <span style="color: #FF5722;">' + admin.escape(this.value) + '</span>';

                    //打开标签页
                    location.hash = admin.correctRouter(href)

                    //如果搜索关键词已经打开，则刷新页面即可
                    events.serach.keys || (events.serach.keys = {});
                    events.serach.keys[admin.tabsPage.index] = this.value;
                    if (this.value === events.serach.keys[admin.tabsPage.index]) {
                        events.refresh(othis);
                    }

                    //清空输入框
                    this.value = '';
                }
            });
        }

        //点击消息
        , message: function (othis) {
            othis.find('.layui-badge-dot').remove();
        }

        //弹出主题面板
        , theme: function () {
            admin.popupRight({
                id: 'LAY_adminPopupTheme'
                , success: function () {
                    view(this.id).render('admin/tpl/theme');
                }
            });
        }

        //便签
        , note: function (othis) {
            var mobile = admin.screen() < 2
                , note = layui.data(setter.tableName).note;

            events.note.index = admin.popup({
                title: '便签'
                , shade: 0
                , offset: [
                    '41px'
                    , (mobile ? null : (othis.offset().left - 250) + 'px')
                ]
                , anim: -1
                , id: 'LAY_adminNote'
                , skin: 'layadmin-note layui-anim layui-anim-upbit'
                , content: '<textarea placeholder="内容"></textarea>'
                , resize: false
                , success: function (layero, index) {
                    var textarea = layero.find('textarea')
                        , value = note === undefined ? '便签中的内容会存储在本地，这样即便你关掉了浏览器，在下次打开时，依然会读取到上一次的记录。是个非常小巧实用的本地备忘录' : note;

                    textarea.val(value).focus().on('keyup', function () {
                        layui.data(setter.tableName, {
                            key: 'note'
                            , value: this.value
                        });
                    });
                }
            })
        }

        //全屏
        , fullscreen: function (othis) {
            var SCREEN_FULL = 'layui-icon-screen-full'
                , SCREEN_REST = 'layui-icon-screen-restore'
                , iconElem = othis.children("i");

            if (iconElem.hasClass(SCREEN_FULL)) {
                admin.fullScreen();
                iconElem.addClass(SCREEN_REST).removeClass(SCREEN_FULL);
            } else {
                admin.exitScreen();
                iconElem.addClass(SCREEN_FULL).removeClass(SCREEN_REST);
            }
        }

        //弹出关于面板
        , about: function () {
            admin.popupRight({
                id: 'LAY_adminPopupAbout'
                , success: function () {
                    view(this.id).render('system/about')
                }
            });
        }

        //弹出更多面板
        , more: function () {
            admin.popupRight({
                id: 'LAY_adminPopupMore'
                , success: function () {
                    view(this.id).render('system/more')
                }
            });
        }

        //返回上一页
        , back: function () {
            history.back();
        }

        //主题设置
        , setTheme: function (othis) {
            var index = othis.data('index')
                , nextIndex = othis.siblings('.layui-this').data('index');

            if (othis.hasClass(THIS)) return;

            othis.addClass(THIS).siblings('.layui-this').removeClass(THIS);
            admin.initTheme(index);
        }

        //语言设置
        , setLanguage: function (othis) {
            admin.initLanguage(othis.data('lang'));
            //wy修改
//          location.href = '/';
			location.href = '';
        }
        
        //打开皮肤设置
        , openSetSkin: function () {
        	var contentHtml = '<div style="padding:10px;">';
        	if(layui.admin.skin() == "oa"){
        		contentHtml += '<a layadmin-event="setSkin" data-skin="OA" style="padding:5px;display: inline-block;border:1px solid #107446;">';
        	}else{
        		contentHtml += '<a layadmin-event="setSkin" data-skin="OA" style="padding:5px;display: inline-block;">';
        	}
        	contentHtml += '<img src="static/img/oaSkin.png" style="width:100%;"/>'
				 	+'</a>'
			 	+'</div>'
			 	+'<div style="padding:10px;">';
			if(layui.admin.skin() == ""){ 
				contentHtml += '<a layadmin-event="setSkin" data-skin="" style="padding:5px;display: inline-block;border:1px solid #107446;">';
			}else{
				contentHtml += '<a layadmin-event="setSkin" data-skin="" style="padding:5px;display: inline-block;">';
			}
			contentHtml += '<img src="static/img/defaultSkin.png" style="width:100%;"/>'
				 	+'</a>'
			 	+'</div>';
            layui.layer.open({
			  type: 1, 
			  title: layui.admin.lang()? "Set skin" : "设置皮肤",
			  shadeClose: true,
			  closeBtn: 0,
			  anim: 2,
			  offset: "rt",
			  area: ["250px","100%"],
			  content: contentHtml
			});   
        }
        //皮肤设置
        , setSkin: function (othis) {
            var local = layui.data(setter.tableName);
            layui.data(setter.tableName, {
                key: 'skin'
                , value: othis.data('skin')
            });
			location.href = '';
        }

        //左右滚动页面标签
        , rollPage: function (type, index) {
            var tabsHeader = $('#LAY_app_tabsheader')
                , liItem = tabsHeader.children('li')
                , scrollWidth = tabsHeader.prop('scrollWidth')
                , outerWidth = tabsHeader.outerWidth()
                , tabsLeft = parseFloat(tabsHeader.css('left'));
			
			//当标签长度小于Div宽度时 自动回复left:0px
			var liWidth = 0;
			tabsHeader.children("li").each(function(idx, item){
				liWidth += $(item).outerWidth();
			});
			if(outerWidth > (liWidth+40)){
				tabsHeader.css('left', "0px");
				$(".layadmin-pagetabs").css('padding-left', "40px");
            	$(".layadmin-tabs-control.layui-icon-prev, .layadmin-tabs-control.layui-icon-next").hide();
            	return false;
			}

            //右左往右
            if (type === 'left') {
                if (!tabsLeft && tabsLeft <= 0) return;

                //当前的left减去可视宽度，用于与上一轮的页标比较
                var prefLeft = -tabsLeft - outerWidth;

                liItem.each(function (index, item) {
                    var li = $(item)
                        , left = li.position().left;

                    if (left >= prefLeft) {
                        tabsHeader.css('left', -left);
                        return false;
                    }
                });
            } else if (type === 'auto') { //自动滚动
                (function () {
                    var thisLi = liItem.eq(index), thisLeft;

                    if (!thisLi[0]) return;
                    thisLeft = thisLi.position().left;

                    //当目标标签在可视区域左侧时
                    if (thisLeft < -tabsLeft) {
                        return tabsHeader.css('left', -thisLeft);
                    }

                    //当目标标签在可视区域右侧时
                    if (thisLeft + thisLi.outerWidth() >= outerWidth - tabsLeft) {
                    	//当出现标签页滚动时,显示移动按钮
                    	$(".layadmin-tabs-control.layui-icon-prev, .layadmin-tabs-control.layui-icon-next").show();
						$(".layadmin-pagetabs").css('padding-left', "80px");
                    	
                        var subLeft = thisLeft + thisLi.outerWidth() - (outerWidth - tabsLeft);
                        liItem.each(function (i, item) {
                            var li = $(item)
                                , left = li.position().left;

                            //从当前可视区域的最左第二个节点遍历，如果减去最左节点的差 > 目标在右侧不可见的宽度，则将该节点放置可视区域最左
                            if (left + tabsLeft > 0) {
                                if (left - tabsLeft > subLeft) {
                                    tabsHeader.css('left', -left);
                                    return false;
                                }
                            }
                        });
                    }
                }());
            } else {
                //默认向左滚动
                liItem.each(function (i, item) {
                    var li = $(item)
                        , left = li.position().left;

                    if (left + li.outerWidth() >= outerWidth - tabsLeft) {
                        tabsHeader.css('left', -left);
                        return false;
                    }
                });
            }
        }

        //向右滚动页面标签
        , leftPage: function () {
            events.rollPage('left');
        }

        //向左滚动页面标签
        , rightPage: function () {
            events.rollPage();
        }

        //关闭当前标签页
        , closeThisTabs: function () {
            admin.closeThisTabs();
        }

        //关闭其它标签页
        , closeOtherTabs: function (type) {
            var TABS_REMOVE = 'LAY-system-pagetabs-remove';
            if (type === 'all') {
                $(TABS_HEADER + ':gt(0)').remove();
                $(APP_BODY).find('.' + TABS_BODY + ':gt(0)').remove();
            } else {
                $(TABS_HEADER).each(function (index, item) {
                    if (index && index != admin.tabsPage.index) {
                        $(item).addClass(TABS_REMOVE);
                        admin.tabsBody(index).addClass(TABS_REMOVE);
                    }
                });
                $('.' + TABS_REMOVE).remove();
            }
        }

        //关闭全部标签页
        , closeAllTabs: function () {
            events.closeOtherTabs('all');
            location.hash = '';
        }

        //遮罩
        , shade: function () {
            admin.sideFlexible();
        }
    };

    //初始
    !function () {
        //主题初始化，本地主题记录优先，其次为 initColorIndex
        var local = layui.data(setter.tableName);
        if (local.theme) {
            admin.theme(local.theme);
        } else if (setter.theme) {
            admin.initTheme(setter.theme.initColorIndex);
        }

        if (local.language) 
            admin.language(local.language);
         else 
            admin.initLanguage(setter.lang||'');
        

        //禁止水平滚动
        $body.addClass('layui-layout-body');

        //移动端强制不开启页面标签功能
        if (admin.screen() < 1) {
            delete setter.pageTabs;
        }

        //不开启页面标签时
        if (!setter.pageTabs) {
            container.addClass('layadmin-tabspage-none');
        }

        //低版本IE提示
        if (device.ie && device.ie < 10) {
            view.error('IE' + device.ie + '下访问可能不佳，推荐使用：Chrome / Firefox / Edge 等高级浏览器', {
                offset: 'auto'
                , id: 'LAY_errorIE'
            });
        }

    }();

    //admin.prevRouter = {}; //上一个路由

    //监听 hash 改变侧边状态
    admin.on('hash(side)', function (router) {
        var path = router.path, getData = function (item) {
            return {
                list: item.children('.layui-nav-child')
                , name: item.data('name')
                , jump: item.data('jump')
            }
        }
            , sideMenu = $('#' + SIDE_MENU)
            , SIDE_NAV_ITEMD = 'layui-nav-itemed'

            //捕获对应菜单
            , matchMenu = function (list) {
                var pathURL = admin.correctRouter(path.join('/'));
                list.each(function (index1, item1) {
                    var othis1 = $(item1)
                        , data1 = getData(othis1)
                        , listChildren1 = data1.list.children('dd')
                        , matched1 = path[0] == data1.name || (index1 === 0 && !path[0])
                            || (data1.jump && pathURL == admin.correctRouter(data1.jump));

                    listChildren1.each(function (index2, item2) {
                        var othis2 = $(item2)
                            , data2 = getData(othis2)
                            , listChildren2 = data2.list.children('dd')
                            , matched2 = (path[0] == data1.name && path[1] == data2.name)
                                || (data2.jump && pathURL == admin.correctRouter(data2.jump));

                        listChildren2.each(function (index3, item3) {
                            var othis3 = $(item3)
                                , data3 = getData(othis3)
                                , matched3 = (path[0] == data1.name && path[1] == data2.name && path[2] == data3.name)
                                    || (data3.jump && pathURL == admin.correctRouter(data3.jump))

                            if (matched3) {
                                var selected = data3.list[0] ? SIDE_NAV_ITEMD : THIS;
                                othis3.addClass(selected).siblings().removeClass(selected); //标记选择器
                                return false;
                            }

                        });

                        if (matched2) {
                            var selected = data2.list[0] ? SIDE_NAV_ITEMD : THIS;
                            othis2.addClass(selected).siblings().removeClass(selected); //标记选择器
                            return false
                        }

                    });

                    if (matched1) {
                        var selected = data1.list[0] ? SIDE_NAV_ITEMD : THIS;
                        othis1.addClass(selected).siblings().removeClass(selected); //标记选择器
                        return false;
                    }

                });
            }

        //重置状态
        sideMenu.find('.' + THIS).removeClass(THIS);

        //移动端点击菜单时自动收缩
        if (admin.screen() < 2) admin.sideFlexible();

        //开始捕获
        matchMenu(sideMenu.children('li'));
    });

    //监听侧边导航点击事件
    element.on('nav(layadmin-system-side-menu)', function (elem) {
        if (elem.siblings('.layui-nav-child')[0] && container.hasClass(SIDE_SHRINK)) {
            admin.sideFlexible('spread');
            layer.close(elem.data('index'));
        };
        admin.tabsPage.type = 'nav';
    });

    //监听选项卡的更多操作
    element.on('nav(layadmin-pagetabs-nav)', function (elem) {
        var dd = elem.parent();
        dd.removeClass(THIS);
        dd.parent().removeClass(SHOW);
    });

    //同步路由
    var setThisRouter = function (othis) {
        var layid = othis.attr('lay-id')
            , attr = othis.attr('lay-attr')
            , index = othis.index();

        location.hash = layid === setter.entry ? '/' : (attr || '/');
        admin.tabsBodyChange(index);
        admin.tabsMenuChange();
    }
        , TABS_HEADER = '#LAY_app_tabsheader>li';

    //页面标签点击
    $body.on('click', TABS_HEADER, function () {
        var othis = $(this)
            , index = othis.index();

        admin.tabsPage.type = 'tab';
        admin.tabsPage.index = index;
        //如果是iframe类型的标签页
        if (othis.attr('lay-attr') === 'iframe') {
            return admin.tabsBodyChange(index);
        }



        setThisRouter(othis); //同步路由
        admin.runResize(); //执行resize事件，如果存在的话
        admin.resizeTable(); //重置当前主体区域的表格尺寸
    });

    //监听 tabspage 删除
    element.on('tabDelete(layadmin-layout-tabs)', function (obj) {
        var othis = $(TABS_HEADER + '.layui-this');

        obj.index && admin.tabsBody(obj.index).remove();
        setThisRouter(othis);

        //移除resize事件
        admin.delResize();
    });

    //页面跳转
    $body.on('click', '*[lay-href]', function () {
        var othis = $(this)
            , href = othis.attr('lay-href')
            , router = layui.router();
		
		if(href.indexOf("http") > -1){
			window.open(href);
		}else{
			admin.tabsPage.elem = othis;
	        //admin.prevRouter[router.path[0]] = router.href; //记录上一次各菜单的路由信息
	
	        //执行跳转
	        location.hash = admin.correctRouter(href);
		}
    });

    //点击事件
    $body.on('click', '*[layadmin-event]', function () {
        var othis = $(this)
            , attrEvent = othis.attr('layadmin-event');
        events[attrEvent] && events[attrEvent].call(this, othis);
    });

    //tips
    $body.on('mouseenter', '*[lay-tips]', function () {
        var othis = $(this);

        if ((othis.attr("shrink") && !container.hasClass(SIDE_SHRINK)) || (othis.parent().hasClass('layui-nav-item') && !container.hasClass(SIDE_SHRINK))){
        	return;
        }

        var tips = othis.attr('lay-tips')
            , offset = othis.attr('lay-offset')
            , direction = othis.attr('lay-direction')
            , index = layer.tips(tips, this, {
                tips: direction || 1
                , time: -1
                , success: function (layero, index) {
                    if (offset) {
                        layero.css('margin-left', offset + 'px');
                    }
                }
            });
        othis.data('index', index);
    }).on('mouseleave', '*[lay-tips]', function () {
        layer.close($(this).data('index'));
    });

    //窗口resize事件
    var resizeSystem = layui.data.resizeSystem = function () {
        //layer.close(events.note.index);
        layer.closeAll('tips');

        if (!resizeSystem.lock) {
            setTimeout(function () {
                admin.sideFlexible(admin.screen() < 2 ? '' : 'spread');
                delete resizeSystem.lock;
            }, 100);
        }

        resizeSystem.lock = true;
    };
    $win.on('resize', layui.data.resizeSystem);

    //接口输出
    exports('admin', admin);
});
