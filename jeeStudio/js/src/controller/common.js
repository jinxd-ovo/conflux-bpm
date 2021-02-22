/**

 @Name：layuiAdmin 公共业务
 @Author：贤心
 @Site：http://www.layui.com/admin/
 @License：LPPL
    
 */

layui.define(['table', 'treeGrid', 'form', 'formSelects', 'upload', 'layedit'], function (exports) {
    var $ = layui.$
        , layer = layui.layer
        , laytpl = layui.laytpl
        , setter = layui.setter

        , view = layui.view
        , admin = layui.admin

        , table = layui.table
        , treeGrid = layui.treeGrid
        //, excel = layui.excel
        , form = layui.form
        , upload = layui.upload
        , layedit = layui.layedit

        , R = {


            trimAll: function (str) {
                //去除所有空格
                if (str !== undefined && typeof str==='string')
                    return str.replace(/\s/g, '');
                else
                    return str;
            }
            , trimCodeAll: function (str) {
                //去除所有空格
                if (str !== undefined && typeof str === 'string')
                    return str.replace(/\s/g, '').replace(/&nbsp;/g,'');
                else
                    return str;
            }

            ,replaceClass:function(elem, oldClass, newClass) {

                if ($.isArray(oldClass))
                    $.each(oldClass, function (k, v) {
                        $(elem).removeClass(v);
                    });
                else
                    $(elem).removeClass(oldClass);

                if ($.isArray(newClass))
                    $.each(newClass, function (k, v) {
                        $(elem).addClass(v);
                    });
                else
                    $(elem).addClass(newClass);
            }

            , hasArray: function (data) {
                return $.isArray(data) && data.length > 0;
            }
            , hasObject: function (data) {
                return $.isPlainObject(data) && !$.isEmptyObject(data);
            }
            , jsonClone: function (data) {
                return JSON.parse(JSON.stringify(data));
            }
            , stringify: function (obj) {
                return JSON.stringify(obj, function (key, val) {
                    if (typeof val === 'function') {
                        return val + '';
                    }
                    return val;
                });
            }
            , parse: function (str) {
                return JSON.parse(str, function (k, v) {
                    if (v.indexOf && v.indexOf('function') > -1) {
                        return eval("(function(){return " + v + " })()");
                    }
                    return v;
                });
            }
            , jsonSort: function (array, field, reverse) {
                //数组长度小于2 或 没有指定排序字段 或 不是json格式数据
                if (array.length < 2 || !field || typeof array[0] !== "object") return array;
                //数字类型排序
                if (typeof array[0][field] === "number") {
                    array.sort(function (x, y) { return x[field] - y[field]; });
                }
                //字符串类型排序
                if (typeof array[0][field] === "string") {
                    array.sort(function (x, y) { return x[field].localeCompare(y[field]); });
                }
                //倒序
                if (reverse) {
                    array.reverse();
                }
                return array;
            }
            , lang: admin.lang

            , confirm: function (title, msg, callback) {
                layer.confirm(msg, { title: (title !== '' ? title : (layui.admin.lang() ? 'affirm' : '操作确认')), icon: 3 }, callback);
            }
            , getFilterFormData: function (filter) {
                var elem = '[lay-filter="' + filter + '"] ';
                var fields = {};
                $(elem + 'input:checked').each(function () {
                    if (eval('fields.' + $(this).attr('name'))) {
                        fields[$(this).attr('name')] += ',' + $(this).val();
                    } else {
                        fields[$(this).attr('name')] = $(this).val();
                    }
                });
                $(elem + 'input:password').each(function () {
                    fields[$(this).attr('name')] = CryptoJS.MD5($(this).val()).toString();
                });
                $(elem + '[lay-skin="switch"]').each(function () {
                    if ($(this).prop('checked')) fields[$(this).attr('name')] = 1;
                    if (!$(this).prop('checked')) fields[$(this).attr('name')] = 0;
                });

                return fields;
            }
            , regular: {
                required: /[\S]+/,
                int: /^-?\d+$/,
                chinese: /^[\u4e00-\u9fa5]+$/,
                email: /^[\w\-\.]+@[\w\-\.]+(\.\w+)+$/,
                phone: /^[1][3,4,5,6,7,8,9][0-9]{9}$/,
                telephone: /^((0\d{2,3})-?)(\d{7,8})(-(\d{3,}))?$/,
                url: /http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/,
                identity: /(^\d{15}$)|(^\d{17}(x|X|\d)$)/,
                dateTime: /^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\s+(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d$/,
                dateTime1: /^[1-9]\d{3}\/(0[1-9]|1[0-2])\/(0[1-9]|[1-2][0-9]|3[0-1])\s+(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d$/
            }
            , verify: {
                required: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';
                    var title = $(item).attr('fieldname');
                    title = title ? title : '';

                    if (!value || $.trim(value) === '')
                        return $(item).attr('requiredmsg') ? $(item).attr('requiredmsg') : title + (layui.admin.lang() ? " can't be empty.":'不能为空。');

                    value = $.trim(value);

                    var vLen = R.trimAll(value).length;
                    if (vLen < $(item).attr('minLen'))
                        return title + (layui.admin.lang() ? " length should not be less than " : '长度不能小于') + $(item).attr('minLen') + (layui.admin.lang() ? '.':'。');

                    if (vLen > $(item).attr('maxLen'))
                        return title + (layui.admin.lang() ? " length should not be greater than " : '长度不能大于') + $(item).attr('maxLen') + (layui.admin.lang() ? '.' : '。');

                    var regular = $(item).attr('regular');
                    var regularMsg = $(item).attr('regularMsg' + layui.admin.lang());
                    if (regular && !new RegExp(regular).test(value))
                        return regularMsg ? regularMsg : title + (layui.admin.lang()?' does not meet the requirements.':'不符合要求。');
                },
                fillRequired: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if (value) {
                        value = $.trim(value);

                        var title = $(item).attr('fieldname');
                        title = title ? title : '';

                        var vLen = R.trimAll(value).length;
                        if (vLen < $(item).attr('minLen'))
                            return title + (layui.admin.lang() ? " length should not be less than " : '长度不能小于') + $(item).attr('minLen') + (layui.admin.lang() ? '.' : '。');

                        if (vLen > $(item).attr('maxLen'))
                            return title + (layui.admin.lang() ? " length should not be greater than " : '长度不能大于') + $(item).attr('maxLen') + (layui.admin.lang() ? '.' : '。');

                        var regular = $(item).attr('regular');
                        var regularMsg = $(item).attr('regularMsg' + layui.admin.lang());
                        if (regular && !new RegExp(regular).test(value))
                            return regularMsg ? regularMsg : (layui.admin.lang() ? ' does not meet the requirements.' : '不符合要求。');
                    }
                },
                visibleRequired: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if ($(item).is(':visible')) {
                        var title = $(item).attr('fieldname');
                        title = title ? title : '';

                        if (!value || $.trim(value) === '')
                            return $(item).attr('requiredmsg') ? $(item).attr('requiredmsg') : title + (layui.admin.lang() ? " can't be empty." : '不能为空。');
                    }
                },
                latreg: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if ($(item).is(':visible')) {
                        var title = $(item).attr('fieldname');
                        title = title ? title : '';

                        if (value) {
                            var latreg = /^(\-|\+)?([0-8]?\d{1}\.\d{0,6}|90\.0{0,6}|[0-8]?\d{1}|90)$/;
                            if (latreg.test(value))
                                return $(item).attr('requiredmsg') ? $(item).attr('requiredmsg'):layui.admin.lang() ? 'The latitude integer part is 0-90 and the decimal part is 0-6 bits.' : '纬度整数部分为0-90,小数部分为0到6位。';
                        }
                    }
                },
                int: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if (value) {
                        value = $.trim(value);

                        var title = $(item).attr('fieldname');
                        title = title ? title : '';

                        if (!R.regular.int.test(value))
                            return title + (layui.admin.lang() ? " only integers can be integer." : '只能输入整数。');

                        var intVal = parseInt(value);
                        if (intVal < $(item).attr('minVal'))
                            return title + (layui.admin.lang() ? " can't be less than " : '不能小于') + $(item).attr('minVal') + (layui.admin.lang() ? '.' : '。');

                        if (intVal > $(item).attr('maxVal'))
                            return title + (layui.admin.lang() ? " can't be greater than " : '不能大于') + $(item).attr('maxVal') + (layui.admin.lang() ? '.' : '。');
                    }
                },
                decimal: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if (value) {
                        value = $.trim(value);

                        var title = $(item).attr('fieldname');
                        title = title ? title : '';

                        if (isNaN(value))
                            return title + (layui.admin.lang() ? " only numbers can be entered." : '只能输入数字。');

                        var decimalVal = parseFloat(value);
                        if (decimalVal < $(item).attr('minVal'))
                            return title + (layui.admin.lang() ? " can't be less than " : '不能小于') + $(item).attr('minVal') + (layui.admin.lang() ? '.' : '。');

                        if (decimalVal > $(item).attr('maxVal'))
                            return title + (layui.admin.lang() ? " can't be greater than " : '不能大于') + $(item).attr('maxVal') + (layui.admin.lang() ? '.' : '。');

                        var decimalPlace = value.length - value.indexOf('.') - 1;
                        if (value.indexOf('.') > -1 && decimalPlace > $(item).attr('decimalPlace'))
                            return title + (layui.admin.lang() ? " no more than " : '不能超过') + $(item).attr('decimalPlace') + (layui.admin.lang() ? " decimal places. " : '位小数。');
                    }
                },
                chinese: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if (value) {
                        value = $.trim(value);

                        var title = $(item).attr('fieldname');
                        title = title ? title : '';

                        if (!R.regular.chinese.test(value))
                            return title + '只能输入中文汉字！';

                        var vLen = R.trimAll(value).length;
                        if (vLen < $(item).attr('minLen'))
                            return title + '字数不能小于' + $(item).attr('minLen') + '！';

                        if (vLen > $(item).attr('maxLen'))
                            return title + '字数不能大于' + $(item).attr('maxLen') + '！';
                    }
                },
                email: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if (value && !R.regular.email.test($.trim(value)))
                        return layui.admin.lang() ? " Please enter the correct e-mail. " : '请输入正确的电子邮箱。';
                },
                phone: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if (value && !R.regular.phone.test($.trim(value)))
                        return layui.admin.lang() ? " Please enter the correct mobile phone number. " : '请输入正确的手机号码。';
                },
                telephone: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if (value && !R.regular.telephone.test($.trim(value)))
                        return layui.admin.lang() ? " Please enter the correct landline. " : '请输入正确的固定电话。';
                },
                url: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if (value && !R.regular.url.test($.trim(value)))
                        return layui.admin.lang() ? " Please enter the correct address. " : '请输入正确的网址。';
                },
                regular: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if (value) {
                        value = $.trim(value);

                        if (value && (value.substr(value.length - 1) === '/' || value.substr(0, 1) === '/'))
                            return '请输入正确的正则表达式，无需首位"/"！';
                    }
                },
                dateTime: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    if (!R.regular.dateTime.test($.trim(value)))
                        return layui.admin.lang() ? " Please enter the correct date. " : '请输入正确的日期。';
                },
                checked: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    var title = $(item).attr('fieldname');
                    title = title ? title : '';

                    var checkedLen = $(item).find('input:checked').length;
                    if (checkedLen === 0)
                        return $(item).attr('choosemsg') ? $(item).attr('choosemsg') : (layui.admin.lang() ? " Please choose " : '请选择') + title + (layui.admin.lang() ? "." : '。');

                    var minChecked = $(item).attr('minChecked');
                    if (checkedLen < minChecked)
                        return title + '最少需要选中' + minChecked + '项！';

                    var maxChecked = $(item).attr('maxChecked');
                    if (checkedLen > maxChecked)
                        return title + '最多只能选中' + maxChecked + '项！';
                },
                selected: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    var title = $(item).attr('fieldname');
                    title = title ? title : '';
                    value = $.trim(value);
                    if (!value || value === '')
                        return $(item).attr('choosemsg') ? $(item).attr('choosemsg') : ((layui.admin.lang() ? " Please choose " : '请选择') + title + (layui.admin.lang() ? "." : '。'));
                },
                selects: function (value, item) {
                    if ($(item).closest('[reitemid]').attr('noverify') !== undefined)
                        return '';

                    var oldItem = $(item).parent().parent().prev();
                    var title = oldItem.attr('fieldname');
                    title = title ? title : '';

                    var checkedLen = value ? value.split(',').length : 0;
                    if (checkedLen === 0)
                        return oldItem.attr('choosemsg') ? oldItem.attr('choosemsg') : ((layui.admin.lang() ? " Please choose " : '请选择') + title + (layui.admin.lang() ? "." : '。'));

                    var minChecked = oldItem.attr('minChecked');
                    if (checkedLen < minChecked)
                        return title + '最少需要选中' + minChecked + '项！';

                    var maxChecked = oldItem.attr('maxChecked');
                    if (checkedLen > maxChecked)
                        return title + '最多只能选中' + maxChecked + '项！';
                }
            }

            , timeFormat_YMD: admin.timeFormat_YMD
            , timeFormat_YMDH: admin.timeFormat_YMDH
            , timeFormat_YMDHM: admin.timeFormat_YMDHM
            , timeFormat_YMDHMS: admin.timeFormat_YMDHMS
            , getExcelDate: admin.getExcelDate
            , getYMDHMS: admin.getYMDHMS

            , initForm: function (options) {
                options = options || {};

                form.render(null, options.formLay);

                if (options.subUrl){
                    form.verify(R.verify);
	                form.on('submit(' + options.formLay + '-submit)', function (data) {
	
	                    var subData = $.extend(data.field, R.getFilterFormData(options.formLay));
	
	                    if (typeof options.verify === 'function' && options.verify.call(this, subData) === false)
	                        return false;
	
	                    //提交 Ajax 成功后，关闭当前弹层并重载表格
	                    admin.req({
	                        url: options.subUrl
	                        //wy修改
	//                      , type: 'post'
	                        , type: options.type ? options.type :'post'
	                        , data: subData
	                        , loading: options.postLoad !== false ? true : false
	                        , tipOk: options.tipOk !== false ? true : false
	                        , success: options.success || null
	                        , done: options.done || null
	                    });
	
	                    return false;
	                });
	            }
            }
            , popupForm: function (options) {
                admin.popup({
                    title: options.title
                    , area: [options.width || '900px', options.height || '700px']
                    , id: 'LAY-popup-' + options.formLay
                    , shadeClose: false
                    , success: function (layero, index) {
                        view(this.id).render(options.url, typeof options.initData === 'function' ? options.initData.call(this) : options.initData || null).done(function () {
                            R.setFormLang(options.formLay);
                            //form.render(null, options.formLay);

                            typeof options.formInit === 'function' && options.formInit.call(this, typeof options.initData === 'function' ? options.initData.call(this) : options.initData || null);

                            //监听提交
                            var success = options.success;
                            var done = options.done;
                            R.initForm($.extend({}, options, {
                                success: function (res) {
                                    typeof success === 'function' && success.call(this, res);
                                }
                                , done: function (res) {
                                    var resultZdy = res.result;
                                    if (resultZdy.indexOf("false") != -1) {

                                    } else {
                                        layer.close(index);
                                        typeof done === 'function' && done.call(this, res, index);
                                    }
                                }
                            }));
                        });
                    },end:options.end
                });
            }
            , tblForm: function (options) {
                view(options.tplId).render(options.url, typeof options.initData === 'function' ? options.initData.call(this) : options.initData || null).done(function () {
                    R.setFormLang(options.formLay);
                    //form.render(null, options.formLay);

                    //typeof options.formInit === 'function' && options.formInit.call(this, typeof options.initData === 'function' ? options.initData.call(this) : options.initData || null);

                    //监听提交
                    var success = options.success;
                    R.initForm($.extend({}, options, {
                        success: function (res) {
                            typeof success === 'function' && success.call(this, res);
                        }
                    }));
                });
            }
            , viewImg: function (imgUrl) {
                R.popupForm({
                    title: layui.admin.lang() ? 'View Image' : '查看图片'
                    , width: '80%'
                    , height: '85%'
                    ,url:'/admin/system/viewImg'
                    ,formLay: 'admin-viewImg-form'
                    , initData: { ImgUrl: imgUrl }
                });
            }

            , getSearchWhere: function (tableId) {
                var fields = {};
                var formDate = $('#' + tableId + '-search-form').serializeArray();
                $.each(formDate, function () {
                    fields[this.name] = this.value;
                });

                var searchWhere = [];
                $.each(fields, function (k, v) {
                    if (!v) {
                        delete fields[k];
                        return;
                    }
                    searchWhere.push({
                        searchValue: v,
                        condition: $('#' + tableId + '-search-form [name="' + k + '"]').attr('condition'),
                        compare: $('#' + tableId + '-search-form [name="' + k + '"]').attr('compare')
                    });
                });
                return { SearchWhere: searchWhere.length > 0 ? JSON.stringify(searchWhere) : null };
            }
            
            , getDataParamsMember: function (table, key) {                
                var item = admin.filterList(admin.getSessionList('data-params-' + table), { member: key + '' })[0];
                return item?item['memberName' + R.lang()]:key;
            }
            , getCustomParamsMember: function (table, key) {
                var item = admin.filterList(admin.getSessionList('custom-params-' + table), { member: key + '' })[0];
                return item ? item['memberName' + R.lang()] : key;
            }
            , setFormLang: function (formid, tableid) {
                var formObj = $('[lay-filter="' + formid + '"]');
                if (!formObj.length)
                    formObj = $('#' + formid);
                if (formObj)
                    $('[lay-filter="' + formid + '-submit"]').val(layui.admin.lang() ? 'Submit' : '确认');

                admin.getDictionary('page-form-field', tableid || formid, function (d) {
                    $.each(d, function (idx, item) {
                        var text = item['memberName' + admin.lang()];
                        var obj = $(formObj).find('[name="' + item.member + '"]');//:not(input:hidden)

                        if (obj && obj.prop('type') !== 'hidden') {
                            //$(obj).parent().prev().html(text);
                            $(obj).parent().prev().hasClass('layui-form-label') ? $(obj).parent().prev().html(text) : '';
                            var tagName = $(obj).prop("tagName");
                            switch (tagName) {
                                case 'SELECT':
                                    $(obj).attr('fieldname', text);
                                    $(obj).children().first().html((admin.lang() ? 'Please select ' : '请选择') + text);
                                    break;
                                case 'INPUT':
                                case 'TEXTAREA':
                                    $(obj).attr('fieldname', text);
                                    typeof $(obj).attr('placeholder') !== 'undefined' ? $(obj).attr('placeholder', (admin.lang() ? 'Please input ' : '请输入') + text) : '';
                                    break;
                                default:
                                    break;
                            }
                        }
                    });

                });
            }
            
            , tableCell: function (table, call) {
                admin.getDictionary('page-table-cell', table, call);
            }

            , tableSearch: function (tableId) {
                table.reload(tableId + '-table', {
                    where: R.getSearchWhere(tableId)
                });
            }

            , operationCell : layui.admin.lang() ? 'Operation' : '操作'
            , initTable: function (options) {
                var tableId = options.tableId;

                if (options.initData)
                    $.each(options.initData, function (k, v) {
                        //admin.removeSessionData('data-params-' + v);
                        admin.updateDataParams(v);
                    });

                var autoTool = options.autoTool === false ? false : true,
                    autoToolbar = options.autoToolbar === false ? false : true;

                var search = $('#' + tableId + '-search-form').length > 0;
                if (search) {
                    R.setFormLang(tableId + '-search-form');
                    var html = '<div class="layui-inline" id="' + tableId + '-form-tool">';//layui-btn-sm
                    if (typeof $('#' + tableId + '-search-form').attr('nosearchbtn') =='undefined') {
                        html += '<button class="layui-btn layui-btn-default layuiadmin-btn-admin" type="button" lay-event="search"><i class="layui-icon layui-icon-search layuiadmin-button-btn"></i></button>';
                        html += '<button class="layui-btn layui-btn-primary" type="button" lay-event="reset">' + (layui.admin.lang() ? 'Reset' : '重置') +'</button>';
                    }
                    if (autoTool)
                        html += admin.getPageToolBtn(tableId);
                    html += '</div>';
                    $('#' + tableId + '-search-form').children().first().append(html);
                    //$('#' + tableId + '-tool').append('<button class="layui-btn layui-btn-default layui-btn-radius" style="margin-left:30px" lay-event="search"><i class="layui-icon layui-icon-search"></i></button><button class="layui-btn layui-btn-primary layui-btn-radius" lay-event="reset"><i class="layui-icon layui-icon-refresh"></i></button>');
                } else {
                    if (autoTool) {
                        var toolHtml = admin.getPageToolBtn(tableId);
                        if (toolHtml.length > 0) {
                            var html = '<form class="layui-form layui-card-header layuiadmin-card-header-auto" id="' + tableId + '-search-form">';
                            html += '<div class="layui-form-item">';
                            html += '<div class="layui-inline" id="' + tableId + '-form-tool">';
                            html += toolHtml;
                            html += '</div>';
                            html += '</div>';
                            html += '</div>';
                            $('#' + tableId + '-table').before(html);
                        }
                    }
                }

                //if (autoTool)
                //    $('#' + tableId + '-tool').html(admin.getPageToolBtn(tableId));

                if (autoToolbar)
                    $('#' + tableId + '-toolbar').html(admin.getPageToolbarBtn(tableId));

                var tableOptions = $.extend({
                    elem: '#' + tableId + '-table'
                    , height: 'full-' + Math.round($('#' + tableId + '-table').offset().top + 30)
                    , autoSort: false
                    , method: 'post'
                    , text: { none: '' }
                    , page: options.tableOptions.page || true
                    //, toolbar: options.tableOptions.toolbar || $('#' + tableId + '-tool').length > 0 ? '#' + tableId + '-tool' : false
                    , defaultToolbar: options.tableOptions.defaultToolbar || []
                    , cellMinWidth: options.tableOptions.cellMinWidth || 120
                    , limit: options.tableOptions.page === false ? null : options.tableOptions.limit || 15
                    , limits: [10, 15, 20, 30, 40, 50, 60, 70, 80, 90]
                    , done: function () {
                        //initImport();
                        initCustomCss();
                    }
                    
                }, options.tableOptions);

                R.tableCell(tableId, function (data) {
                    if (data&&data.length > 0) {
                        $.each(tableOptions.cols[0], function (idx, item) {
                            if (item.field) {
                                var find = admin.filterList(data, { member: item.field })[0];
                                console.log(data)
                                if (find)
                                    item.title = find['memberName' + R.lang()] || item.title;
                            }
                        });
                    }

                    table.render(tableOptions);
                });

                table.on('sort(' + tableId + '-table)', function (obj) {
                    if (tableOptions.page === false ? false : true)
                        table.reload(tableId + '-table', {
                            initSort: obj
                            , page: tableOptions.page
                            , where: { order: obj.field + (obj.type ? ' ' + obj.type : '') }
                        });
                });

                var initCustomCss = function () {
                    if (options.customCss)
                        $('[lay-id="' + tableId + '-table"]').addClass(tableId + '-table');
                };

                var initImport = function () {
                    if (options.importOptions)
                        upload.render({
                            elem: '#' + tableId + '-tool-import' //绑定元素
                            , url: '/upload/' //上传接口（PS:这里不用传递整个 excel）
                            , auto: false //选择文件后不自动上传
                            , accept: 'file'
                            , exts: 'xlsx'
                            , choose: function (obj) {// 选择文件回调

                                //var files = obj.pushFile();
                                //files = Object.values(files);
                                //if (files.length > 1)
                                //    files.splice(0, 1);

                                var files = obj.pushFile();
                                //files = Object.values(files);

                                var file = {};
                                $.each(files, function (k, v) {
                                    file = v;
                                });

                                R.uploadExcel([file], $.extend({}, options.importOptions,
                                    {
                                        cellTitle: options.templateOptions.cellTitle
                                        , cellWidth: options.templateOptions.cellWidth
                                        , success: function (res) {
                                            if (res.code === 0) {
                                                layui.table.reload(tableId + '-table'); //重载表格
                                            }
                                            if (res.code === 1) {
                                                R.exportExcel(res.data, {
                                                    fileName: options.importOptions.fileName
                                                    , cellWidth: $.extend(options.templateOptions.cellWidth, options.importOptions.verifyCellWidth)
                                                    , cellTitle: $.extend(options.templateOptions.cellTitle, options.importOptions.verifyCellTitle)
                                                    , filterExportData: options.importOptions.verifyImportFilter
                                                    , handleImportFilter: options.importOptions.handleImportFilter
                                                });
                                            }
                                        }
                                    }
                                ));
                            }
                        });
                };

                var formOptions = $.extend({
                    width: '900px'
                    , height: '700px'
                }, options.formOptions);

                //操作事件
                var toolfun = $.extend({
                    search: function () {
                        table.reload(tableId + '-table', {
                            where: R.getSearchWhere(tableId)
                        });
                    }
                    , reset: function () {
                        $('#' + tableId + '-search-form')[0].reset();
                        table.reload(tableId + '-table', {
                            page: tableOptions.page
                            , where: $.extend({ SearchWhere: null }, tableOptions.where)
                        });
                    }
                    ,batchdel: function () {
                        var checkStatus = table.checkStatus(tableId + '-table')
                            , checkData = checkStatus.data; //得到选中的数据

                        if (checkData.length === 0) 
                            return layer.msg(layui.admin.lang() ? 'Please select data.' : '请选择数据。');
                        

                        var arr = [];
                        $.each(checkData, function (idx, json) {
                            arr.push(json[options.idField]);
                        });

                        view.confirm('', {
                            title: admin.getToolFormTitle(tableId, 'batchdel')
                            , doyes: function () {
                                admin.req({
                                    url: options.delUrl
                                    , type: 'post'
                                    , data: { ids: arr.join(',') }
                                    , loading: options.postLoad || true
                                    , tipOk: options.tipOk || true
                                    , success: function (res) {
                                        if (res.code === 0) {
                                            layui.table.reload(tableId + '-table'); //重载表格
                                            toolfun['batchdel_success'] ? toolfun['batchdel_success'].call(this, res) : '';
                                        }

                                    }
                                });
                            }
                        });
                    }
                    , clear: function () {
                        view.confirm('', {
                            title: admin.getToolFormTitle(tableId, 'clear')
                            , doyes: function () {
                                admin.req({
                                    url: options.clearUrl
                                    , type: 'post'
                                    , loading: options.postLoad || true
                                    , tipOk: options.tipOk || true
                                    , success: function (res) {
                                        if (res.code === 0) {
                                            layui.table.reload(tableId + '-table'); //重载表格
                                            toolfun['clear_success'] ? toolfun['clear_success'].call(this, res) : '';
                                        }

                                    }
                                });
                            }
                        });
                    }
                    , add: function () {
                        R.popupForm($.extend({}, formOptions, {
                            title: admin.getToolFormTitle(tableId, 'add')
                            ,formLay: formOptions.formLay || tableId + '-form'
                            , initData: typeof formOptions.initData === 'function' ? formOptions.initData.call(this) : formOptions.initData
                            , success: function (res) {
                                if (res.code === 0) {
                                    layui.table.reload(tableId + '-table'); //重载表格
                                    toolfun['add_success'] ? toolfun['add_success'].call(this, res) : '';
                                }
                            }
                        }));
                    }
                    , template: function () {
                        R.exportExcel([], options.templateOptions);
                    }
                    , export: function (obj) {
                        admin.req({
                            url: tableOptions.url
                            , type: 'post'
                            , data: $.extend(tableOptions.where, R.getSearchWhere(tableId))
                            , loading: options.postLoad || true
                            , tipOk: options.tipOk || true
                            , success: function (res) {

                                if (res.code === 0) {
                                    var columns = [].concat.apply([], obj.config.cols)
                                        , cellTitle = {}
                                        , cellWidth = {}
                                        , filterExportData = {};


                                    var index = 0;
                                    for (var i = 0; i < columns.length; i++) {
                                        if (columns[i].field  && !columns[i].hide) {
                                            filterExportData[columns[i].field] = columns[i];
                                            if (columns[i].width) 
                                                cellWidth[String.fromCharCode(64 + parseInt(++index))] = columns[i].width * 0.8;
                                            else
                                                cellWidth[String.fromCharCode(64 + parseInt(++index))] = options.tableOptions.cellMinWidth || 120
                                                    ;
                                            cellTitle[columns[i].field] = columns[i].title;
                                        }
                                    }
                                    $.each(filterExportData, function (k, v) {
                                        filterExportData[k] = function (value, line, data) {
                                            return {
                                                v: v.excel ? (typeof v.excel === 'function' ? v.excel(line) : value) : v.templet ? (typeof v.templet === 'function' ? v.templet(line) : value) : value
                                                , s: {
                                                    alignment: {
                                                        horizontal: v.align
                                                    }
                                                }
                                            };
                                        };
                                    });
                                    

                                    R.exportExcel(res.data, $.extend({},options.exportOptions, {
                                        cellTitle: cellTitle
                                        , cellWidth: cellWidth
                                        , filterExportData: filterExportData
                                    }));
                                }
                            }
                        });
                    }
                    , customExport: function (obj) {
                        admin.req({
                            url: tableOptions.url
                            , type: 'post'
                            , data: $.extend(tableOptions.where, R.getSearchWhere(tableId))
                            , loading: options.postLoad || true
                            , tipOk: options.tipOk || true
                            , success: function (res) {

                                if (res.code === 0) 
                                    R.exportExcel(res.data, options.exportOptions);
                            }
                        });
                    }
                   
                }, options.toolfun);
                //table.on('toolbar(' + tableId + '-table)', function (obj) {
                //    var type = obj.event;
                //    toolfun[type] ? toolfun[type].call(this, obj) : '';
                //});
                $('#' + tableId + '-form-tool .layui-btn').on('click', function () {
                    var type = $(this).attr('lay-event');
                    toolfun[type] ? toolfun[type].call(this) : '';
                });

                //监听工具条
                var toolbarfun = $.extend({
                    edit: function (data) {
                        R.popupForm($.extend({}, formOptions, {
                            title: admin.getToolbarFormTitle(tableId, 'edit')
                            , formLay: formOptions.formLay || tableId + '-form'
                            , initData: $.extend({}, typeof formOptions.initData === 'function' ? formOptions.initData.call(this) : formOptions.initData, data)
                            , success: function (res) {
                                if (res.result === "false_1") {
                                    layer.alert("时间冲突不可分配", {
                                        skin: 'layui-layer-lan'
                                        , closeBtn: 0
                                        , anim: 1
                                    });
                                } else if (res.result === "flase_2") {
                                    layer.alert("截止时间不能小于开始时间", {
                                        skin: 'layui-layer-lan'
                                        , closeBtn: 0
                                        , anim: 1
                                    });
                                } else if (res.result === "false_seat") {
                                    layer.alert("编码不可重复", {
                                        skin: 'layui-layer-lan'
                                        , closeBtn: 0
                                        , anim: 1
                                    });
                                } else if (res.result === "true_0") {
                                    layer.alert("操作成功", {
                                        skin: 'layui-layer-lan'
                                        , closeBtn: 0
                                        , anim: 1
                                    });
                                    layui.table.reload(tableId + '-table'); //重载表格
                                    toolbarfun['edit_success'] ? toolbarfun['edit_success'].call(this, res) : '';
                                } else {
                                    layui.table.reload(tableId + '-table'); //重载表格
                                    toolbarfun['edit_success'] ? toolbarfun['edit_success'].call(this, res) : '';
                                }
                                
                            }
                        }));
                    }
                    , viewReservation: function (data) {
                        R.popupForm($.extend({}, formOptions, {
                            title: admin.getToolbarFormTitle(tableId, 'edit')
                            , formLay: formOptions.formLay || tableId + '-form'
                            , initData: $.extend({}, typeof formOptions.initData === 'function' ? formOptions.initData.call(this) : formOptions.initData, data)
                            , success: function (res) {
                                layui.table.reload(tableId + '-table'); //重载表格
                                toolbarfun['edit_success'] ? toolbarfun['edit_success'].call(this, res) : '';
                            }
                        }));
                    }
                    , del: function (data) {
                        view.confirm('', {
                            title: admin.getToolbarFormTitle(tableId, 'del')
                            , doyes: function () {
                                admin.req({
                                    url: options.delUrl
                                    , type: 'post'
                                    , data: { ids: data[options.idField] }
                                    , loading: options.postLoad || true
                                    , tipOk: options.tipOk || true
                                    , success: function (res) {
                                        if (res.code === 0) {
                                            layui.table.reload(tableId + '-table'); //重载表格
                                            toolbarfun['del_success'] ? toolbarfun['del_success'].call(this, res) : '';
                                        }
                                    }
                                });
                            }
                        });
                    }
                }, options.toolbarfun);
                table.on('tool(' + tableId + '-table)', function (obj) {
                    var data = obj.data;
                    var type = obj.event;
                    toolbarfun[type] ? toolbarfun[type].call(this, data) : '';

                });

                typeof options.tableEvent === 'function' && options.tableEvent.call(this);
            }

            , treeGridSearch: function (tableId) {
                treeGrid.query(tableId + '-table', {
                    where: R.getSearchWhere(tableId)
                });
            }
            , initTreeGrid: function (options) {
                var tableId = options.tableId;

                if (options.initData)
                    $.each(options.initData, function (k, v) {
                        admin.updateDataParams(v);
                    });


                var search = $('#' + tableId + '-search-form').length > 0;
                //if (search) {
                //    R.setFormLang(tableId + '-search-form');
                //    $('#' + tableId + '-tool').append('<button class="layui-btn layui-btn-default layui-btn-radius" style="margin-left:30px" lay-event="search"><i class="layui-icon layui-icon-search"></i></button><button class="layui-btn layui-btn-primary layui-btn-radius" lay-event="reset"><i class="layui-icon layui-icon-refresh"></i></button>');
                //}
                if (search) {
                    R.setFormLang(tableId + '-search-form');
                    var html = '<div class="layui-inline" id="' + tableId + '-form-tool">';//layui-btn-sm
                    html += '<button class="layui-btn layui-btn-default layuiadmin-btn-admin" type="button" lay-event="search"><i class="layui-icon layui-icon-search layuiadmin-button-btn"></i></button>';
                    html += '<button class="layui-btn layui-btn-primary" type="button" lay-event="reset">' + (layui.admin.lang() ? 'Reset' :'重置')+'</button>';
                    html += admin.getPageToolBtn(tableId);
                    html += '</div>';
                    $('#' + tableId + '-search-form').children().first().append(html);
                    //$('#' + tableId + '-tool').append('<button class="layui-btn layui-btn-default layui-btn-radius" style="margin-left:30px" lay-event="search"><i class="layui-icon layui-icon-search"></i></button><button class="layui-btn layui-btn-primary layui-btn-radius" lay-event="reset"><i class="layui-icon layui-icon-refresh"></i></button>');
                } else {
                    var toolHtml = admin.getPageToolBtn(tableId);
                   
                    if (toolHtml.length > 0) {
                        var html = '<form class="layui-form layui-card-header layuiadmin-card-header-auto" id="' + tableId + '-search-form">';
                        html += '<div class="layui-form-item">';
                        html += '<div class="layui-inline" id="' + tableId + '-form-tool">';
                        html += toolHtml;
                        html += '</div>';
                        html += '</div>';
                        html += '</div>';
                        $('#' + tableId+'-table').before(html);
                    }
                }

                //if (autoTool)
                //    $('#' + tableId + '-tool').html(admin.getPageToolBtn(tableId));

                var autoToolbar = options.autoToolbar === false ? false : true;
                if (autoToolbar)
                    $('#' + tableId + '-toolbar').html(admin.getPageToolbarBtn(tableId));

                var tableOptions = $.extend({
                    id :tableId + '-table'
                    , elem: '#' + tableId + '-table'
                    , isOpenDefault: options.tableOptions.isOpenDefault||true
                    , iconOpen: options.tableOptions.iconOpen || false
                    , loading: options.tableOptions.loading || false
                    , method: 'post'
                    , request: {
                        sort: null
                    }
                    , heightRemove: ['#' + tableId + '-table', Math.round($('#' + tableId + '-table').offset().top + 30)]//不计算的高度,表格设定的是固定高度，此项不生效
                    , text: { none: '' }
                    , isPage: options.tableOptions.isPage || false
                    , cellMinWidth: options.tableOptions.cellMinWidth || 120
                    , limit: options.tableOptions.isPage === false ? null : options.tableOptions.limit || 15
                    , limits: [10, 15, 20, 30, 40, 50, 60, 70, 80, 90]

                }, options.tableOptions);

                R.tableCell(tableId, function (data) {
                    if (data&&data.length > 0) {
                        $.each(tableOptions.cols[0], function (idx, item) {
                            if (item.field) {
                                var find = admin.filterList(data, { member: item.field })[0];
                                if (find)
                                    item.title = find['memberName' + R.lang()] || item.title;
                            }
                        });
                    }
                       
                    treeGrid.render(tableOptions);
                });
                
                var initImport = function () {
                    if (options.importOptions)
                        upload.render({
                            elem: '#' + tableId + '-tool-import' //绑定元素
                            , url: '/upload/' //上传接口（PS:这里不用传递整个 excel）
                            , auto: false //选择文件后不自动上传
                            , accept: 'file'
                            , exts: 'xlsx'
                            , choose: function (obj) {// 选择文件回调

                                //var files = obj.pushFile();
                                //files = Object.values(files);
                                //if (files.length > 1)
                                //    files.splice(0, 1);

                                var files = obj.pushFile();
                                //files = Object.values(files);

                                var file = {};
                                $.each(files, function (k, v) {
                                    file = v;
                                });

                                R.uploadExcel([file], $.extend({}, options.importOptions,
                                    {
                                        cellTitle: options.templateOptions.cellTitle
                                        , cellWidth: options.templateOptions.cellWidth
                                        , success: function (res) {
                                            if (res.code === 0) {
                                                treeGrid.query(tableId + '-table'); //重载表格
                                            }
                                            if (res.code === 1) {
                                                R.exportExcel(res.data, {
                                                    fileName: options.importOptions.fileName
                                                    , cellWidth: $.extend(options.templateOptions.cellWidth, options.importOptions.verifyCellWidth)
                                                    , cellTitle: $.extend(options.templateOptions.cellTitle, options.importOptions.verifyCellTitle)
                                                    , filterExportData: options.importOptions.verifyImportFilter
                                                    , handleImportFilter: options.importOptions.handleImportFilter
                                                });
                                            }
                                        }
                                    }
                                ));
                            }
                        });
                };
                initImport();

                var formOptions = $.extend({
                    width: '900px'
                    , height: '700px'
                }, options.formOptions);

                //操作事件
                var toolfun = $.extend({
                    search: function () {
                        treeGrid.query(tableId + '-table', {
                            where: $.extend(R.getSearchWhere(tableId), tableOptions.where)
                        });
                    }
                    , reset: function () {
                        $('#' + tableId + '-search-form')[0].reset();
                        treeGrid.query(tableId + '-table', {
                            page: tableOptions.page
                            , where: $.extend({ SearchWhere: null }, tableOptions.where)
                        });
                    }
                    ,batchdel: function () {
                        var checkStatus = treeGrid.checkStatus(tableId + '-table')
                            , checkData = checkStatus.data; //得到选中的数据

                        if (checkData.length === 0)
                            return layer.msg(layui.admin.lang() ? 'Please select data.' : '请选择数据。');

                        var arr = [];
                        $.each(checkData, function (idx, json) {
                            arr.push(json[options.tableOptions.idField]);
                        });

                        view.confirm('', {
                            title: admin.getToolFormTitle(tableId, 'batchdel')
                            , doyes: function () {
                                admin.req({
                                    url: options.delUrl
                                    , type: 'post'
                                    , data: { ids: arr.join(',') }
                                    , loading: options.postLoad || true
                                    , tipOk: options.tipOk || true
                                    , success: function (res) {
                                        if (res.code === 0) {
                                            treeGrid.query(tableId + '-table'); //重载表格
                                            toolfun['batchdel_success'] ? toolfun['batchdel_success'].call(this, res) : '';
                                        }
                                    }
                                });
                            }
                        });
                    }
                    , clear: function () {
                        view.confirm('', {
                            title: admin.getToolFormTitle(tableId, 'clear')
                            , doyes: function () {
                                admin.req({
                                    url: options.clearUrl
                                    , type: 'post'
                                    , loading: options.postLoad || true
                                    , tipOk: options.tipOk || true
                                    , success: function (res) {
                                        if (res.code === 0) {
                                            treeGrid.query(tableId + '-table'); //重载表格
                                            toolfun['clear_success'] ? toolfun['clear_success'].call(this, res) : '';
                                        }

                                    }
                                });
                            }
                        });
                    }
                    , add: function () {
                        R.popupForm($.extend({}, formOptions, {
                            title: admin.getToolFormTitle(tableId, 'add')
                            , formLay: formOptions.formLay || tableId + '-form'
                            , initData: typeof formOptions.initData === 'function' ? formOptions.initData.call(this) : formOptions.initData
                            , success: function (res) {
                                if (res.code === 0) {
                                    treeGrid.query(tableId + '-table'); //重载表格
                                    toolfun['add_success'] ? toolfun['add_success'].call(this, res) : '';
                                }
                            }
                        }));
                    }
                    , template: function () {
                        R.exportExcel([], options.templateOptions);
                    }
                    , export: function () {
                        admin.req({
                            url: tableOptions.url
                            ,type:'post'
                            , data: $.extend(tableOptions.where, R.getSearchWhere(tableId))
                            , loading: options.postLoad || true
                            , tipOk: options.tipOk || true
                            , success: function (res) {

                                if (res.code === 0) {
                                    var columns = [].concat.apply([], tableOptions.cols[0])
                                        , cellTitle = {}
                                        , cellWidth = {}
                                        , filterExportData = {};


                                    var index = 0;
                                    for (var i = 0; i < columns.length; i++) {
                                        if (columns[i].field && !columns[i].hide) {
                                            filterExportData[columns[i].field] = columns[i];
                                            if (columns[i].width)
                                                cellWidth[String.fromCharCode(64 + parseInt(++index))] = columns[i].width * 0.8;
                                            else
                                                cellWidth[String.fromCharCode(64 + parseInt(++index))] = options.tableOptions.cellMinWidth || 120
                                                    ;
                                            cellTitle[columns[i].field] = columns[i].title;
                                        }
                                    }
                                    $.each(filterExportData, function (k, v) {
                                        filterExportData[k] = function (value, line, data) {
                                            return {
                                                v: v.excel ? (typeof v.excel === 'function' ? v.excel(line) : value) : v.templet ? (typeof v.templet === 'function' ? v.templet(line) : value) : value
                                                , s: {
                                                    alignment: {
                                                        horizontal: v.align
                                                    }
                                                }
                                            };
                                        };
                                    });


                                    R.exportExcel(res.data, $.extend({}, options.exportOptions, {
                                        cellTitle: cellTitle
                                        , cellWidth: cellWidth
                                        , filterExportData: filterExportData
                                    }));
                                }
                            }
                        });
                    }
                    , customExport: function () {
                        admin.req({
                            url: tableOptions.url
                            , type: 'post'
                            , data: $.extend(tableOptions.where, R.getSearchWhere(tableId))
                            , loading: options.postLoad || true
                            , tipOk: options.tipOk || true
                            , success: function (res) {

                                if (res.code === 0)
                                    R.exportExcel(res.data, options.exportOptions);
                            }
                        });
                    }

                }, options.toolfun);

                $('#' + tableId+'-form-tool .layui-btn').on('click', function () {
                    var type = $(this).attr('lay-event');
                    toolfun[type] ? toolfun[type].call(this) : '';
                });

                //监听工具条
                var toolbarfun = $.extend({
                    edit: function (data) {
                        R.popupForm($.extend({}, formOptions, {
                            title: admin.getToolbarFormTitle(tableId, 'edit')
                            , formLay: formOptions.formLay || tableId + '-form'
                            , initData: $.extend({}, typeof formOptions.initData === 'function' ? formOptions.initData.call(this) : formOptions.initData, data)
                            , success: function (res) {
                                if (res.code === 0) {
                                    treeGrid.query(tableId + '-table'); //重载表格
                                    toolbarfun['edit_success'] ? toolbarfun['edit_success'].call(this, res) : '';
                                }
                            }
                        }));
                    }
                    , del: function (data) {
                        view.confirm('', {
                            title: admin.getToolbarFormTitle(tableId, 'del')
                            , doyes: function () {
                                admin.req({
                                    url: options.delUrl
                                    , type: 'post'
                                    , data: { ids: data[options.tableOptions.idField] }
                                    , loading: options.postLoad || true
                                    , tipOk: options.tipOk || true
                                    , success: function (res) {
                                        if (res.code === 0) {
                                            treeGrid.query(tableId + '-table'); //重载表格
                                            toolbarfun['del_success'] ? toolbarfun['del_success'].call(this, res) : '';
                                        }
                                    }
                                });
                            }
                        });
                    }
                }, options.toolbarfun);

                treeGrid.on('tool(' + tableId + '-table)', function (obj) {
                    var data = obj.data;
                    var type = obj.event;
                    toolbarfun[type] ? toolbarfun[type].call(this, data) : '';
                });
            }

            , exportExcel: function (data, options) {
                options = options || {};

                // 生成配置的辅助函数，返回结果作为扩展功能的配置参数传入即可
                // 1. 复杂表头合并[B1,C1,D1][E1,F1]
                var mergeConf = excel.makeMergeConfig($.extend([], options.mergeConf));
                // 2. B列宽 150，F列宽200，默认80
                var colConf = excel.makeColConfig($.extend({}, options.cellWidth), options.cellDefaultWidth || 120);
                
                // 3. 第1行行高40，第二行行高30，默认20
                var rowConf = excel.makeRowConfig($.extend({}, options.rowHeight), options.rowDefaultHeight || 20);
                // 4. 公式的用法
                //data.push({
                //    id: '',
                //    username: '总年龄',
                //    age: { t: 'n', f: 'SUM(C4:C10)' },
                //    sex: '总分',
                //    score: { t: 'n', f: 'SUM(E4:E10)' },
                //    classify: {
                //        v: '注意：保护模式中公式无效，请「启用编辑」',
                //        s: { font: { color: { rgb: 'FF0000' } } }
                //    }
                //});

                if (options.sortField)
                    R.jsonSort(data, options.sortField, true);

                if (!options.filterExportData) {
                    options.filterExportData = {};
                    $.each(options.cellTitle, function (k, v) {
                        options.filterExportData[k] = k;
                    });
                }

                data = excel.filterExportData(data, options.filterExportData);

                if (options.cellTitle)
                    data.unshift(options.cellTitle);

                //5. 样式
                excel.setExportCellStyle(data, options.titleRange || 'A1:' + String.fromCharCode(64 + Object.keys(options.cellTitle).length)+1, $.extend({
                    s: {
                        alignment: {
                            horizontal: 'center',vertical: 'center'
                        }
                        ,font: { sz: 12, bold: true }
                        , border: {
                            top: {
                                style: 'thin',
                                color: { indexed: 64 }
                            },
                            bottom: {
                                style: 'thin',
                                color: { indexed: 64 }
                            },
                            left: {
                                style: 'thin',
                                color: { indexed: 64 }
                            },
                            right: {
                                style: 'thin',
                                color: { indexed: 64 }
                            }
                        }
                    }
                }, options.titleStyle));

                if (data.length > 1)
                    excel.setExportCellStyle(data, options.cellRange || 'A2:' + String.fromCharCode(64 + Object.keys(options.cellTitle).length) + data.length, $.extend({
                        s: {
                            alignment: {
                                vertical: 'center'
                            }
                            , font: { sz: 11 }
                            , border: {
                                top: {
                                    style: 'thin',
                                    color: { indexed: 64 }
                                },
                                bottom: {
                                    style: 'thin',
                                    color: { indexed: 64 }
                                },
                                left: {
                                    style: 'thin',
                                    color: { indexed: 64 }
                                },
                                right: {
                                    style: 'thin',
                                    color: { indexed: 64 }
                                }
                            }
                        }
                    }, options.cellStyle));

                excel.exportExcel({
                    sheet1: data
                }, (options.fileName || '导出') + '_' + R.getYMDHMS(new Date()) + '.xlsx', 'xlsx', {
                        extend: {
                            '!merges': mergeConf
                            , '!cols': colConf
                            , '!rows': rowConf
                        }
                    });
            }

            , uploadExcel: function (file, options) {
                try {
                    excel.importExcel(file, $.extend({
                        header: 0,
                        fields: options.cellTitle
                    }, options.importTitleConf), function (data) {
                        var sheetData = [];
                        $.each(data[0], function (idx, sheet) {
                            if (R.hasArray(sheet) && sheet.length > 0) {
                                $.merge(sheetData, sheet);
                                if (options.mergeSheet === false) return false;
                            }
                        });

                        if (sheetData.length === 0)
                            return view.warn('没有可导入的数据。');

                        if (options.verifyData(sheetData) || false) {
                            view.warn('导入数据验证失败。');
                            return R.exportExcel(sheetData, {
                                fileName: options.fileName
                                , cellWidth: $.extend(options.cellWidth, options.verifyCellWidth)
                                , cellTitle: $.extend(options.cellTitle, options.verifyCellTitle)
                                , filterExportData: options.verifyImportFilter
                            });
                        }

                        if (options.handleImportFilter)
                            sheetData = excel.filterExportData(sheetData, options.handleImportFilter);


                        admin.req({
                            url: options.url
                            , type: 'post'
                            , data: { datas: JSON.stringify(sheetData) }
                            , loading: options.postLoad || true
                            , tipOk: options.tipOk || true
                            , success: options.success || null
                        });

                    });
                } catch (e) {
                    return view.error(e.message);
                }
            }

            , initEditor: function (options) {
                //构建一个默认的编辑器
                var editor = layedit.build(options.editorId, $.extend({
                    tool: [
                        'strong' //加粗
                        , 'italic' //斜体
                        , 'underline' //下划线
                        , 'del' //删除线

                        , '|' //分割线

                        , 'left' //左对齐
                        , 'center' //居中对齐
                        , 'right' //右对齐
                        , 'link' //超链接
                        , 'unlink' //清除链接
                    ]
                    , height: 400
                }, options.editorOptions));

                //编辑器外部操作
                var fun = $.extend({
                    content: function () {
                        return layedit.getContent(editor); //获取编辑器内容
                    }
                    , setContent: function (html) {
                        layedit.setContent(editor, html, false);
                    }
                    , text: function () {
                        return layedit.getText(editor); //获取编辑器纯文本内容
                    }
                    , selection: function () {
                        return layedit.getSelection(editor);
                    }
                    , sync: function () {
                        layedit.sync(editor);
                    }
                }, options.fun);
                return fun;
            }
        };

    //公共业务的逻辑处理可以写在此处，切换任何页面都会执行
    //……

    

    //对外暴露的接口
    exports('common', R);
});