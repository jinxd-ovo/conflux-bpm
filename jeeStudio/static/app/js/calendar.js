/**
 * Created by mengmei on 17/2/23.
 */

/********************** 自制日历开始 ****************/
Array.prototype.contains = function (obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}
var holiday = new Array("2017-12-30","2017-12-31","2018-1-1","2018-2-15","2018-2-16","2018-2-17","2018-2-18","2018-2-19","2018-2-20","2018-2-21","2018-4-5","2018-4-6","2018-4-7","2018-4-29","2018-4-30","2018-5-1","2018-6-16","2018-6-17","2018-6-18","2018-9-22","2018-9-23","2018-9-24","2018-10-1","2018-10-2","2018-10-3","2018-10-4","2018-10-5","2018-10-6","2018-10-7");

var workday = new Array("2018-2-11","2018-2-24","2018-4-8","2018-4-28","2018-9-29","2018-9-30");


var NYear;     //现在的年
var NMonth;    //现在的月
var NDate;     //现在的日
var year;      //年
var month;     //月
var date;      //日
var lastDate;  //本月最后一天
var pastMonth_lastDate; //上个月最后一天
var firstWeek; //本月第一天星期几
var lastDayWeek;  //本月最后一天星期几
var rows;      //本月日历的行数
var weeks = new Array('日','一','二','三','四','五','六','七','八','九','十');
var firstRowDays;  //第一行有几天
var startYear = 1900;
var endYear = 2050;

var yearNotes = "";
var monthNotes = "";

var screenWidth = window.screen.availWidth;
if(screenWidth > 560){
    screenWidth = 560;
}
var pageDivNum = 0;

var prePageYear, prePageMonth, nextPageYear, nextPageMonth;
var canLeft = 0;  //是否可以向左滑
var canRight = 0; //是否可以向右滑


$(function(){
    //计算: 年份、月份
    var myDate = new Date();
    NYear = myDate.getFullYear();
    NMonth = myDate.getMonth();
    NDate = myDate.getDate();
    year = myDate.getFullYear();
    month = myDate.getMonth();
    date = myDate.getDate();

    //计算: 本月第一天星期几、本月最后一天、当月行数
    setDatas(myDate);

    //画日历
    initialCalendar();

    //渲染样式
    $("."+NYear+"-"+NMonth+"-"+NDate).addClass("today current"); //今天背景色
    $(".lunarText").hide(); //tip背面文字隐藏


    //事件
    //上月
    $("#headRow").on("click", "#preMonth", function () {
        rightSwipe();
        //显示天气图标
        showeather();
    });

    //下月
    $("#headRow").on("click", "#nextMonth", function () {
        leftSwipe();
        //显示天气图标
        showeather();
    });

    //返回今天
    $("#backToday").on("click", function(){
        year=NYear;
        month=NMonth;
        date=NDate;
        var _myDate = new Date();
        _myDate.setFullYear(year, month, date);

        //刷新内容
        refreshCalendar(_myDate);
        //显示天气
        showeather();
    });

    //改变年
    $("#yearsSelect").on("change", function(){
        year = $(this).val();
        var _myDate = new Date();
        _myDate.setFullYear(year, month, date);
        refreshCalendar2(_myDate);
        //显示天气
        showeather();
    });

    //改变月份
    $("#monthsSelect").on("change", function(){
        month = $(this).val();
        var _myDate = new Date();
        _myDate.setFullYear(year, month, date);
        refreshCalendar2(_myDate);
        //显示天气
        showeather();
    });


    //点击日历事件
    $("#view").on("click", ".pageTable .dayBox", function(e){
        e.stopPropagation();
        $(".tip").hide();
        var idStr = $(this).attr("id");
        var strs = idStr.split("-");
        if((strs[0]>=1900 && strs[0]<=2050) && strs[1]!=month){ //在年限范围内,且不是本月
            if((strs[0]==year && strs[1]>month) || (strs[0]>year)){
                leftSwipe();
            }else{
                rightSwipe();
            }
            //显示天气图标
            showeather();
        }else {
            $(".current").removeClass("current");
            $("." + idStr).addClass("current");
            //显示提示
            $(".tip").hide();
            if ($(this).find(".temperature").text() != "") {
                /*$(this).find(".tip").show();*/
                $(this).find(".weatherText").show();
                $(this).find(".lunarText").hide();
            } else {
                /*$(this).find(".tip").show();*/
                $(this).find(".lunarText").show();
                $(this).find(".weatherText").hide();
                $(this).find(".lunarText .flip").hide();
            }
            
            if(typeof calendarClick == "function"){
				calendarClick(idStr)
            }
        }
    });
    //点击空白处
    $("body").on("click", ":not(.dayBox)", function(){
        $(".tip").css("display","none");
    });

    //翻转tip
    $("#view").on("click", ".weatherText .flip", function(e){
        $(this).parents(".weatherText").hide();
        /*$(".lunarText", $(this).parents(".tip")).show();*/
        e.stopPropagation();
    });
    $("#view").on("click", ".lunarText .flip", function(e){
        /*$(".weatherText", $(this).parents(".tip")).show();*/
        $(this).parents(".lunarText").hide();
        e.stopPropagation();
    });

    //滑动翻页
    swipe();
});


function refreshCalendar(_myDate){
    //上月下月按钮
    refreshBtn();
    //计算: 本月第一天星期几、本月最后一天、当月行数
    setDatas(_myDate);
    //刷新日期表格
    __refreshDates();
    //渲染样式
    afterRenderDates();
}

//后置渲染
function afterRenderDates() {
    //头部
    $("#yearsSelect option:selected").removeAttr("selected");
    $("#yearsSelect").val(year);
    $("#monthsSelect option:selected").removeAttr("selected");
    $("#monthsSelect").val(month);

    //渲染样式
    $("."+NYear+"-"+NMonth+"-"+NDate).addClass("today current"); //今天背景色
}

function refreshCalendar2(_myDate){ //不刷新头部
    //上月下月按钮
    refreshBtn();
    //计算: 本月第一天星期几、本月最后一天、当月行数
    setDatas(_myDate);
    //刷新日期表格
    __refreshDates();
    //渲染样式
    $("."+NYear+"-"+NMonth+"-"+NDate).addClass("today current"); //今天背景色
}

function refreshBtn() {
    if(year==2050 && month==11){
        $("#nextMonth").attr("disabled","disabled");
    }else{
        $("#nextMonth").removeAttr("disabled");
    }
    if(year==1900 && month==0){
        $("#preMonth").attr("disabled","disabled");
    }else{
        $("#preMonth").removeAttr("disabled");
    }
}

//计算: 本月第一天星期几、本月最后一天、当月行数
function setDatas(_myDate){
    __getLastDate(_myDate);
    __getRows();
}

//计算: 本月第一天星期几、上个月最后一天、本月最后一天、本月最后一天星期几(私有)
function __getLastDate(_myDate){
    var tempDate = _myDate;
    tempDate.setDate(1); //本月第一天星期几
    firstWeek = tempDate.getDay();
    tempDate.setTime(tempDate.getTime()-86400000); //上个月最后一天, 这个月1日的前一天(减去一天的毫秒值86400000)
    pastMonth_lastDate = tempDate.getDate();

    if(month==11){ //本月最后一天,是下月1日的前一天(减去一天的毫秒值86400000)
        _myDate.setFullYear(parseInt(year)+1, 0, 1);
    }else{
        _myDate.setMonth(parseInt(month)+1, 1);
    }
    _myDate.setTime(_myDate.getTime()-86400000);
    lastDate = _myDate.getDate();
    lastDayWeek = _myDate.getDay();  //本月最后一天星期几
}

//计算当月行数(私有)
function __getRows(){
    firstRowDays = 7-firstWeek;                //第一行有几天
    var otherRows = (lastDate-firstRowDays)/7; //小数行数
    var middleRows = otherRows.toFixed(0);     //中间整数行
    if(otherRows-middleRows>0){  //有小数部分
        rows = parseInt(middleRows)+2;
    }else{
        rows = parseInt(middleRows)+1;
    }
}


//渲染日历
function initialCalendar(){
    //初始化日历头部
    __initialHeader();

    //显示星期行
    __showWeeks();

    //渲染日期
    var dateTbody = '<tbody id="dateTbody">'
        +'<tr id="tr2">'+''
        +'  <td colspan="7" id="view"></td>'
        +'</tr></tbody>';
    $("#calendar").append(dateTbody);
    __refreshDates();
}

//初始化日历头部
function __initialHeader(){
    //日历头部行
    var headRow = '<tr><td id="headRow" class="row" colspan="7"></td></tr>';
    $("#calendar").append(headRow);

    //年份下拉框
    var yearsSelect = '<select id="yearsSelect" class="form-control"></select>';
    $("#headRow").append(yearsSelect);
    var yearsOption = '';
    for(var i=startYear; i<=endYear; i++){
        if(i==NYear){
            yearsOption += '<option value="'+i+'" selected>'+i+'年'+'</option>';
        }else{
            yearsOption += '<option value="'+i+'">'+i+'年'+'</option>';
        }
    }
    $("#yearsSelect").append(yearsOption);

    //月份下拉框
    var monthsSelect = '<div class="btn-group">'
        +	 '<button type="button" class="btn btn-default" id="preMonth"> < </button>'
        +	 '<select id="monthsSelect" class="form-control"></select>'
        +	 '<button type="button" class="btn btn-default" id="nextMonth"> > </button>'
        +'</div>';
    $("#headRow").append(monthsSelect);
    var monthsOption = '';
    for(var i=0; i<=11; i++){
        if(i==(NMonth)){
            monthsOption += '<option value="'+i+'" selected>'+(i+1)+'月'+'</option>';
        }else{
            monthsOption += '<option value="'+i+'">'+(i+1)+'月'+'</option>';
        }
    }
    $("#monthsSelect").append(monthsOption);

    //返回今天按钮
    var backToday = '<button class="btn btn-default" id="backToday">返回今天</button>';
    $("#headRow").append(backToday);
}

//显示星期行
function __showWeeks(){
    var weekRow = '<tr id="weekRow"></tr>';
    $("#calendar").append(weekRow);

    var weekCells = '';
    for(var i=0; i<=6; i++){
        weekCells += '<th>'+weeks[i]+'</th>';
    }
    $("#weekRow").append(weekCells);
}

//空的页面,加一页日期
function __refreshDates(){
    $("#view").empty();
    __appendDates();
    //增加左右两边表格
    addPageDiv();
}
function __appendDates() {
	//wy
	if(yearNotes != year || monthNotes != month){
		yearNotes = year;
		monthNotes= month;
		
		var wydata = {
			month: yearNotes + '-' + (monthNotes.length == 1 ? "0" + monthNotes : monthNotes)
		}
		app.ajax('app/meeting/getworkday', 'get', wydata, function(result){
			workday = result.data.workDay;
		},false);
	}
	
    $("#view").append('<div class="pageDiv"><table class="pageTable"></table></div>');
    for(var i=0; i<rows; i++){
        var dateRow = '<tr class="dateRow'+pageDivNum+"_"+i+'"></tr>';
        $(".pageTable").last().append(dateRow);
        for(var j=1; j<=7; j++){
            var dateCell = '<th><div class="dayBox ';
            if(j==1 || j==7){
                dateCell += 'weekend';
            }

            var yearNum,monthNum,dayNum;
            if(i==0){
                if((j-1)<firstWeek){
                    dateCell += ' notCurrentMonth ';
                    dayNum = pastMonth_lastDate-firstWeek+j;
                    yearNum = (month==0?year-1:year);
                    monthNum = (month==0?11:month-1);
                }else{
                    dayNum = j-firstWeek;
                    yearNum = year;
                    monthNum = month;
                }
            }else{
                dayNum = firstRowDays+(i-1)*7+j;
                yearNum = ((month==11)&&(dayNum>lastDate) ? (parseInt(year)+1) : year);
                monthNum = ((dayNum>lastDate) ? (month==11?0:parseInt(month)+1) : month);
                var nextMonth_dayNum = j-(parseInt(lastDayWeek)+1);
                if(dayNum > lastDate){
                    dateCell += ' notCurrentMonth ';
                    dayNum = nextMonth_dayNum;
                }
            }
            dateCell += ' '+yearNum+'-'+monthNum+'-'+dayNum+'" id="'+yearNum+'-'+monthNum+'-'+dayNum+'">'+dayNum;

            //增加节日显示
            var dateObj = new Date();
            dateObj.setFullYear(yearNum, monthNum, dayNum);
            var lunarObj = lunar(dateObj);
            var fes = lunar(dateObj).festival();
            if(fes && fes.length>0){
                dateCell += "<div class='lunar cTerm'>"+$.trim(fes[0].desc)+"</div>";
            }
            //无节日,增加节气显示
            var cTerm = lunar(dateObj).term;
            if((!fes || fes.length==0) && cTerm){
                dateCell += "<div class='lunar cTerm'>"+cTerm+"</div>";
            }
            //无节日和节气时，增加农历显示
            if(!cTerm && (!fes || fes.length==0)){
                var lunarDate = lunar(dateObj).lDate;
                dateCell += "<div class='lunar cDate'>";
                if(lunarDate=="初一"){
                    dateCell += lunar(dateObj).lMonth + "月";
                }else{
                    dateCell += lunarDate;
                }
                dateCell += "</div>";
            }
            //提示
            if(j>1 && j<7){
                dateCell += '<span class="tip">'
                    +     '<span class="weatherText">'
                    +           '<span class="city"><i class="icon-location2"></i><span class="cityName"></span><span class="flip"><i class="icon-calendar2"></i></span></span>'
                    +           '<span class="temperature"></span>'
                    +     '</span>'
                    +     '<span class="lunarText"><p class="lunarYear">'+lunarObj.gzYear+'年 ['+lunarObj.animal+'年]'+'</p><p class="lunarMonthDay">'+lunarObj.lMonth+'月'+lunarObj.lDate+'</p><span class="flip"><i class="icon-temprature"></i></span></span>'
                    +     '<span class="arrow">'
                    +         '<svg xmlns="http://www.w3.org/2000/svg">'
                    +             '<polygon points="0 0 10 0 5 10" fill="#c94520"></polygon>'
                    +         '</svg>'
                    +     '</span>'
                    + '</span>';
            }else if(j==7){
                dateCell += '<span class="rTip tip">'
                    +     '<span class="weatherText">'
                    +           '<span class="city"><i class="icon-location2"></i><span class="cityName"></span><span class="flip"><i class="icon-calendar2"></i></span></span>'
                    +           '<span class="temperature"></span>'
                    +     '</span>'
                    +     '<span class="lunarText"><p class="lunarYear">'+lunarObj.gzYear+'年 ['+lunarObj.animal+'年]'+'</p><p class="lunarMonthDay">'+lunarObj.lMonth+'月'+lunarObj.lDate+'</p><span class="flip"><i class="icon-temprature"></i></span></span>'
                    +     '<span class="rArrow arrow">'
                    +         '<svg xmlns="http://www.w3.org/2000/svg">'
                    +             '<polygon points="0 0 10 0 15 10" fill="#c94520"></polygon>'
                    +         '</svg>'
                    +     '</span>'
                    + '</span>';
            }else if(j==1){
                dateCell += '<span class="lTip tip">'
                    +     '<span class="weatherText">'
                    +           '<span class="city"><i class="icon-location2"></i><span class="cityName"></span><span class="flip"><i class="icon-calendar2"></i></span></span>'
                    +           '<span class="temperature"></span>'
                    +     '</span>'
                    +     '<span class="lunarText"><p class="lunarYear">'+lunarObj.gzYear+'年 ['+lunarObj.animal+'年]'+'</p><p class="lunarMonthDay">'+lunarObj.lMonth+'月'+lunarObj.lDate+'</p><span class="flip"><i class="icon-temprature"></i></span></span>'
                    +     '<span class="lArrow arrow">'
                    +         '<svg xmlns="http://www.w3.org/2000/svg">'
                    +             '<polygon points="10 0 20 0 5 10" fill="#c94520"></polygon>'
                    +         '</svg>'
                    +     '</span>'
                    + '</span>';
            }

            //法定节假日
            /*if(holiday.contains(yearNum+'-'+(parseInt(monthNum)+1)+'-'+dayNum)){
                dateCell += '<div class="holiday">休</div>';
            }
            if(workday.contains(yearNum+'-'+(parseInt(monthNum)+1)+'-'+dayNum)){
                dateCell += '<div class="workday">班</div>';
            }*/
            //wy
            var wyMonth = (parseInt(monthNum)+1) < 10 ? "0"+(parseInt(monthNum)+1) : (parseInt(monthNum)+1);
            var wyDayNum = dayNum < 10 ? "0"+dayNum : dayNum;
            var wyToday = yearNum+'-'+wyMonth+'-'+wyDayNum;
           	var wyArr = $.grep(workday, function(item,index){
            	return item.date == wyToday;
            }) 
            if(wyArr.length > 0){
            	if(wyArr[0].type == 10){
            		dateCell += '<div class="holiday">休</div>';
            	}else if(wyArr[0].type == 20){
            		dateCell += '<div class="workday">班</div>';
            	}
            }
            //天气
            dateCell += '<div class="weather"></div>';
            dateCell += '</div></th>';
            $(".dateRow"+pageDivNum+"_"+i).last().append(dateCell);
        }
    }
    pageDivNum++;
}
function __prependDates() {
	//wy
	if(yearNotes != year || monthNotes != month){
		yearNotes = year;
		monthNotes= month;
		
		var wydata = {
			month: yearNotes + '-' + (monthNotes.length == 1 ? "0" + monthNotes : monthNotes)
		}
		app.ajax('app/meeting/getworkday', 'get', wydata, function(result){
			workday = result.data.workDay;
		},false);
	}
	
    $("#view").prepend('<div class="pageDiv"><table class="pageTable"></table></div>');
    for(var i=0; i<rows; i++){
        var dateRow = '<tr class="dateRow'+pageDivNum+"_"+i+'"></tr>';
        $(".pageTable").first().append(dateRow);
        for(var j=1; j<=7; j++){
            var dateCell = '<th><div class="dayBox ';
            if(j==1 || j==7){
                dateCell += 'weekend';
            }
            var yearNum,monthNum,dayNum;
            if(i==0){
                if((j-1)<firstWeek){
                    dateCell += ' notCurrentMonth ';
                    dayNum = pastMonth_lastDate-firstWeek+j;
                    yearNum = (month==0?year-1:year);
                    monthNum = (month==0?11:month-1);
                }else{
                    dayNum = j-firstWeek;
                    yearNum = year;
                    monthNum = month;
                }
            }else{
                dayNum = firstRowDays+(i-1)*7+j;
                yearNum = ((month==11)&&(dayNum>lastDate) ? (parseInt(year)+1) : year);
                monthNum = ((dayNum>lastDate) ? (month==11?0:parseInt(month)+1) : month);
                var nextMonth_dayNum = j-(parseInt(lastDayWeek)+1);
                if(dayNum > lastDate){
                    dateCell += ' notCurrentMonth ';
                    dayNum = nextMonth_dayNum;
                }
            }
            dateCell += ' '+yearNum+'-'+monthNum+'-'+dayNum+'" id="'+yearNum+'-'+monthNum+'-'+dayNum+'">'+dayNum;

            //增加节日显示
            var dateObj = new Date();
            dateObj.setFullYear(yearNum, monthNum, dayNum);
            var lunarObj = lunar(dateObj);
            var fes = lunar(dateObj).festival();
            if(fes && fes.length>0){
                dateCell += "<div class='lunar cTerm'>"+$.trim(fes[0].desc)+"</div>";
            }
            //无节日,增加节气显示
            var cTerm = lunar(dateObj).term;
            if((!fes || fes.length==0) && cTerm){
                dateCell += "<div class='lunar cTerm'>"+cTerm+"</div>";
            }
            //无节日和节气时，增加农历显示
            if(!cTerm && (!fes || fes.length==0)){
                var lunarDate = lunar(dateObj).lDate;
                dateCell += "<div class='lunar cDate'>";
                if(lunarDate=="初一"){
                    dateCell += lunar(dateObj).lMonth + "月";
                }else{
                    dateCell += lunarDate;
                }
                dateCell += "</div>";
            }
            //提示
            if(j>1 && j<7){
                dateCell += '<span class="tip">'
                    +     '<span class="weatherText">'
                    +           '<span class="city"><i class="icon-location2"></i><span class="cityName"></span><span class="flip"><i class="icon-calendar2"></i></span></span>'
                    +           '<span class="temperature"></span>'
                    +     '</span>'
                    +     '<span class="lunarText"><p class="lunarYear">'+lunarObj.gzYear+'年 ['+lunarObj.animal+'年]'+'</p><p class="lunarMonthDay">'+lunarObj.lMonth+'月'+lunarObj.lDate+'</p><span class="flip"><i class="icon-temprature"></i></span></span>'
                    +     '<span class="arrow">'
                    +         '<svg xmlns="http://www.w3.org/2000/svg">'
                    +             '<polygon points="0 0 10 0 5 10" fill="#c94520"></polygon>'
                    +         '</svg>'
                    +     '</span>'
                    + '</span>';
            }else if(j==7){
                dateCell += '<span class="rTip tip">'
                    +     '<span class="weatherText">'
                    +           '<span class="city"><i class="icon-location2"></i><span class="cityName"></span><span class="flip"><i class="icon-calendar2"></i></span></span>'
                    +           '<span class="temperature"></span>'
                    +     '</span>'
                    +     '<span class="lunarText"><p class="lunarYear">'+lunarObj.gzYear+'年 ['+lunarObj.animal+'年]'+'</p><p class="lunarMonthDay">'+lunarObj.lMonth+'月'+lunarObj.lDate+'</p><span class="flip"><i class="icon-temprature"></i></span></span>'
                    +     '<span class="rArrow arrow">'
                    +         '<svg xmlns="http://www.w3.org/2000/svg">'
                    +             '<polygon points="0 0 10 0 15 10" fill="#c94520"></polygon>'
                    +         '</svg>'
                    +     '</span>'
                    + '</span>';
            }else if(j==1){
                dateCell += '<span class="lTip tip">'
                    +     '<span class="weatherText">'
                    +           '<span class="city"><i class="icon-location2"></i><span class="cityName"></span><span class="flip"><i class="icon-calendar2"></i></span></span>'
                    +           '<span class="temperature"></span>'
                    +     '</span>'
                    +     '<span class="lunarText"><p class="lunarYear">'+lunarObj.gzYear+'年 ['+lunarObj.animal+'年]'+'</p><p class="lunarMonthDay">'+lunarObj.lMonth+'月'+lunarObj.lDate+'</p><span class="flip"><i class="icon-temprature"></i></span></span>'
                    +     '<span class="lArrow arrow">'
                    +         '<svg xmlns="http://www.w3.org/2000/svg">'
                    +             '<polygon points="10 0 20 0 5 10" fill="#c94520"></polygon>'
                    +         '</svg>'
                    +     '</span>'
                    + '</span>';
            }

            //法定节假日
            /*if(holiday.contains(yearNum+'-'+(parseInt(monthNum)+1)+'-'+dayNum)){
                dateCell += '<div class="holiday">休</div>';
            }
            if(workday.contains(yearNum+'-'+(parseInt(monthNum)+1)+'-'+dayNum)){
                dateCell += '<div class="workday">班</div>';
            }*/
            //wy
            var wyMonth = (parseInt(monthNum)+1) < 10 ? "0"+(parseInt(monthNum)+1) : (parseInt(monthNum)+1);
            var wyDayNum = dayNum < 10 ? "0"+dayNum : dayNum;
            var wyToday = yearNum+'-'+wyMonth+'-'+wyDayNum;
           	var wyArr = $.grep(workday, function(item,index){
            	return item.date == wyToday;
            }) 
            if(wyArr.length > 0){
            	if(wyArr[0].type == 10){
            		dateCell += '<div class="holiday">休</div>';
            	}else if(wyArr[0].type == 20){
            		dateCell += '<div class="workday">班</div>';
            	}
            }
            //天气
            dateCell += '<div class="weather"></div>';
            dateCell += '</div></th>';
            $(".dateRow"+pageDivNum+"_"+i).first().append(dateCell);
        }
    }
    pageDivNum++;
}

//上一月
function preMonth(){
    var thisYear = year;
    var thisMonth = month;
    if(year==1900 && month==0){
        return [0, thisYear, thisMonth];
    }else{
        if(month==0){
            year--;
            month = 11;
        }else{
            month--;
        }
        var _myDate = new Date();
        _myDate.setFullYear(year, month, 15);

        //计算: 本月第一天星期几、本月最后一天、当月行数
        setDatas(_myDate);
        //在左边追加一个日期表格
        __prependDates();
        $(".pageDiv").first().css("left", -screenWidth+"px");

        //设置灰色背景
        $(".pageDiv").first().addClass("notCurPage");
        //还原数据
        var tempYear = year;
        var tempMonth = month;
        year = thisYear;
        month = thisMonth;
        thisYear = tempYear;
        thisMonth = tempMonth;
        return [1, thisYear, thisMonth];
    }
}
//下一月
function nextMonth(){
    var thisYear = year;
    var thisMonth = month;
    if(year==2050 && month==11){
        return [0, thisYear, thisMonth];
    }else {
        if (month == 11) {
            year++;
            month = 0;
        } else {
            month++;
        }
        var _myDate = new Date();
        _myDate.setFullYear(year, month, 15);

        //计算: 本月第一天星期几、本月最后一天、当月行数
        setDatas(_myDate);

        //在右边追加一个日期表格
        __appendDates();
        $(".pageDiv").last().css("left", screenWidth + "px");

        //设置灰色背景
        $(".pageDiv").last().addClass("notCurPage");
        //还原数据
        var tempYear = year;
        var tempMonth = month;
        year = thisYear;
        month = thisMonth;
        thisYear = tempYear;
        thisMonth = tempMonth;
        return [1,thisYear,thisMonth];
    }
}



//点击下个月时调用
function leftSwipe() {
        if (canLeft) {
            //整体向左平移
            if($(".pageDiv").size()==3) {
                $(".pageDiv").first().remove();
            }
            $(".pageDiv").each(function () {
                this.style.transition = "-webkit-transform 300ms linear";
                this.style.webkitTransform = "translate(-"+screenWidth+"px, 0px)";
            });
            year = nextPageYear;
            month = nextPageMonth;
            afterRenderDates();
            canLeft = 0;
            canRight = 0;
            $(".pageDiv").last().removeClass("notCurPage");
            $(".pageDiv").eq($(".pageDiv").size()-2).addClass("notCurPage");
            //删除多余的表格,将当前页位归 0px
            setTimeout(function () {
                $(".pageDiv").first().remove();
                $(".pageDiv").last()[0].style.transition = "";
                $(".pageDiv").last()[0].style.webkitTransform = "translate("+0+"px, 0px)";
                $(".pageDiv").last().css("left","0px");

                addPageDiv();
            }, 300);
        }
}

//点击上个月时调用
function rightSwipe() {
        if (canRight) {
            //整体向右平移
            if($(".pageDiv").size()==3) {
                $(".pageDiv").last().remove();
            }
            $(".pageDiv").each(function () {
                this.style.transition = "-webkit-transform 300ms linear";
                this.style.webkitTransform = "translate("+screenWidth+"px, 0px)";
            });
            year = prePageYear;
            month = prePageMonth;
            afterRenderDates();
            canLeft = 0;
            canRight = 0;
            $(".pageDiv").first().removeClass("notCurPage");
            $(".pageDiv").eq(1).addClass("notCurPage");
            //删除多余的表格,将当前页位归 0px
            setTimeout(function () {
                $(".pageDiv").last().remove();
                $(".pageDiv").first()[0].style.transition = "";
                $(".pageDiv").first()[0].style.webkitTransform = "translate("+0+"px, 0px)";
                $(".pageDiv").first().css("left","0px");

                addPageDiv();
            }, 300);
        }
}

function addPageDiv() {
    //追加表格
    if ($(".pageDiv").size()==1){
        //向左追加表格,设置位置
        var preArr = preMonth();
        canRight = preArr[0];
        prePageYear = preArr[1];
        prePageMonth = preArr[2];
        //向右追加表格,设置位置
        var nextArr = nextMonth();
        canLeft = nextArr[0];
        nextPageYear = nextArr[1];
        nextPageMonth = nextArr[2];
    }
}


//滑动处理
function swipe() {
    var view = document.getElementById('view');
    var startX, startY, moveEndX, moveEndY, X, Y, startT;
    var curYear, curMonth;

    //滑动开始
    view.addEventListener('touchstart', function(e) {
        //记录开始时鼠标坐标
        startX = e.touches[0].clientX;
        startY = e.touches[0].clientY;
        //记录手指按下开始时间
        startT = new Date().getTime();
        //记录当前月
        curYear = year;
        curMonth = month;
    }, false);

    //滑动时
    view.addEventListener('touchmove', function(e) {
        moveEndX = e.changedTouches[0].clientX;
        moveEndY = e.changedTouches[0].clientY;
        X = moveEndX - startX;
        Y = moveEndY - startY;
        if ( Math.abs(X) > Math.abs(Y) ) { //横向滑动
            e.preventDefault();
            if ( (X > 0 && canRight==1) || (X < 0 && canLeft==1) ) {
                //去掉过渡效果,跟着鼠标滑动
                $(".pageDiv").each(function () {
                    this.style.transition = "";
                    this.style.webkitTransform = "translate("+X+"px, 0px)";
                });
                //变颜色
                if( Math.abs(X) > screenWidth*0.5 &&  X > 0 ){ //向右滑动过半
                    year = prePageYear;
                    month = prePageMonth;
                    //改变透明度
                    $(".pageDiv").first().removeClass("notCurPage");
                    $(".pageDiv").eq(1).addClass("notCurPage");
                }else if( Math.abs(X) > screenWidth*0.5 &&  X < 0 ){ //向左滑动过半
                    year = nextPageYear;
                    month = nextPageMonth;
                    //改变透明度
                    $(".pageDiv").last().removeClass("notCurPage");
                    $(".pageDiv").eq($(".pageDiv").size()-2).addClass("notCurPage");
                }else if( Math.abs(X) < screenWidth*0.5 ){
                    year = curYear;
                    month = curMonth;
                    //还原透明度
                    if(canRight==1){
                        $(".pageDiv").first().addClass("notCurPage");
                        $(".pageDiv").eq(1).removeClass("notCurPage");
                    }
                    if(canLeft==1){
                        $(".pageDiv").last().addClass("notCurPage");
                        $(".pageDiv").eq($(".pageDiv").size()-2).removeClass("notCurPage");
                    }
                }
                afterRenderDates();
            }
        }
    }, false);

    //滑动结束
    view.addEventListener('touchend', function(e) {
        moveEndX = e.changedTouches[0].clientX;
        moveEndY = e.changedTouches[0].clientY;
        X = moveEndX - startX;
        Y = moveEndY - startY;
        var endDate = new Date().getTime();
        //坐标对比
        if ( Math.abs(X) > Math.abs(Y) ) { //横向滑动
            if ( endDate-startT < 300  ||  Math.abs(X) > screenWidth*0.5 ) { //快滑 或 滑动过半
                if ( X > 0 && canRight==1 ) { //向右滑
                    $(".tip").hide();
                    //整体向右平移
                    if($(".pageDiv").size()==3){
                        $(".pageDiv").last().remove();
                    }
                    $(".pageDiv").each(function () {
                        this.style.transition = "-webkit-transform 300ms linear";
                        this.style.webkitTransform = "translate("+screenWidth+"px, 0px)";
                    });
                    year = prePageYear;
                    month = prePageMonth;
                    canLeft = 0;
                    canRight = 0;
                    //改变透明度
                    $(".pageDiv").first().removeClass("notCurPage");
                    $(".pageDiv").eq(1).addClass("notCurPage");
                    //删除多余的表格,将当前页位归 0px
                    setTimeout(function () {
                        $(".pageDiv").last().remove();
                        $(".pageDiv").first()[0].style.transition = "";
                        $(".pageDiv").first()[0].style.webkitTransform = "translate("+0+"px, 0px)";
                        $(".pageDiv").first().css("left","0px");

                        addPageDiv();
                    }, 300);
                } else if ( X < 0 && canLeft==1 ) { //向左滑
                    $(".tip").hide();
                    //整体向左平移
                    if($(".pageDiv").size()==3) {
                        $(".pageDiv").first().remove();
                    }
                    $(".pageDiv").each(function () {
                        this.style.transition = "-webkit-transform 300ms linear";
                        this.style.webkitTransform = "translate(-"+screenWidth+"px, 0px)";
                    });
                    year = nextPageYear;
                    month = nextPageMonth;
                    canLeft = 0;
                    canRight = 0;
                    //改变透明度
                    $(".pageDiv").last().removeClass("notCurPage");
                    $(".pageDiv").eq($(".pageDiv").size()-2).addClass("notCurPage");
                    //删除多余的表格,将当前页位归 0px
                    setTimeout(function () {
                        $(".pageDiv").first().remove();
                        $(".pageDiv").last()[0].style.transition = "";
                        $(".pageDiv").last()[0].style.webkitTransform = "translate("+0+"px, 0px)";
                        $(".pageDiv").last().css("left","0px");

                        addPageDiv();
                    }, 300);
                }
                afterRenderDates();
                //显示天气图标
                showeather();
            } else if ( Math.abs(X) < screenWidth*0.5 ) { //滑动不过半
                $(".pageDiv").each(function () { //整体回滚
                    this.style.transition = "-webkit-transform 300ms linear";
                    this.style.webkitTransform = "translate("+0+"px, 0px)";
                });
                year = curYear;
                month = curMonth;
                //还原透明度
                if(canRight==1){
                    $(".pageDiv").first().addClass("notCurPage");
                    $(".pageDiv").eq(1).removeClass("notCurPage");
                }
                if(canLeft==1){
                    $(".pageDiv").last().addClass("notCurPage");
                    $(".pageDiv").eq($(".pageDiv").size()-2).removeClass("notCurPage");
                }
            }
        }
    });
}



/********************** 天气 *******************/
var city = "";
var province = "";
var w_days;
var weatherArr;
// var instance;

$(function () {
    $("#bac").hide();
    city = getCookie("city");
    province = getCookie("province");
    $.initProv("#pro", "#city", province, city);
    if(city == ""){
        //百度IP定位
        /*$.ajax({
            type: "POST",
            url: "http://api.map.baidu.com/location/ip",
            data: "ak=R6ZO0BTGRposRtC1fRNxaUbDwisZrgu0",
            dataType: "jsonp",
            success: function(msg){
                console.log(msg);
                city = msg.content.address_detail.city;
                province = msg.content.address_detail.province;
                $.initProv("#pro", "#city", province, city);
                // $.initProv();
                //写入cookie
                setCookie("city",city,365);
                setCookie("province",province,365);
                //获取天气预报
                weather();
            }
        });*/
    }else{
        //获取天气预报
        weather();
    }


    //选择城市
    $("#city").change(function () {
        city = $(this).val();
        var num = parseInt($("#pro").val())+1;
        province = $("#pro option:eq("+num+")").attr("pro");
        //写入cookie
        setCookie("city",city,365);
        setCookie("province",province,365);
        $("#bac").hide();
        //获取天气预报
        weather();
    });



    //弹出城市弹窗
    $("#view").on("click", ".weatherText .cityName", function(e){
        e.stopPropagation();
        $("#bac").show();
    });
    $('#cityPop').click(function (e) {
        e.stopPropagation();
    });
    //隐藏城市弹窗
    $("#bac:not(#cityPop)").click(function () {
        $("#bac").hide();
    });
});

//写入cookie
function setCookie(c_name,value,expiredays) {
    var exdate = new Date();
    exdate.setDate(exdate.getDate()+expiredays);
    document.cookie = c_name + "=" + encodeURIComponent(value) +
        ((expiredays==null) ? "" : ";expires="+exdate.toGMTString());
}

//读取cookie
function getCookie(c_name) {
    if (document.cookie.length>0) {
        var c_start = document.cookie.indexOf(c_name + "=");
        if (c_start!=-1) {
            c_start = c_start + c_name.length+1;
            var c_end = document.cookie.indexOf(";",c_start);
            if (c_end==-1) c_end = document.cookie.length;
            return decodeURIComponent(document.cookie.substring(c_start,c_end));
        }
    }
    return ""
}

$._cityInfo = [{"n":"北京市","c":["北京市"]},
    {"n":"天津市","c":["天津市"]},
    {"n":"上海市","c":["上海市"]},
    {"n":"重庆市","c":["重庆市"]},
    {"n":"河北省","c":["石家庄市","唐山市","秦皇岛市","邯郸市","邢台市","保定市","张家口市","承德市","沧州市","廊坊市","衡水市"]},
    {"n":"山西省","c":["太原市","大同市","阳泉市","长治市","晋城市","朔州市","晋中市","运城市","忻州市","临汾市","吕梁市"]},
    {"n":"台湾省","c":["台北市","高雄市","基隆市","台中市","台南市","新竹市","嘉义市","台北县","宜兰县","桃园县","新竹县","苗栗县","台中县","彰化县","南投县","云林县","嘉义县","台南县","高雄县","屏东县","澎湖县","台东县","花莲县"]},
    {"n":"辽宁省","c":["沈阳市","大连市","鞍山市","抚顺市","本溪市","丹东市","锦州市","营口市","阜新市","辽阳市","盘锦市","铁岭市","朝阳市","葫芦岛市"]},
    {"n":"吉林省","c":["长春市","吉林市","四平市","辽源市","通化市","白山市","松原市","白城市","延边朝鲜族自治州"]},
    {"n":"黑龙江省","c":["哈尔滨市","齐齐哈尔市","鹤岗市","双鸭山市","鸡西市","大庆市","伊春市","牡丹江市","佳木斯市","七台河市","黑河市","绥化市","大兴安岭地区"]},
    {"n":"江苏省","c":["南京市","无锡市","徐州市","常州市","苏州市","南通市","连云港市","淮安市","盐城市","扬州市","镇江市","泰州市","宿迁市"]},
    {"n":"浙江省","c":["杭州市","宁波市","温州市","嘉兴市","湖州市","绍兴市","金华市","衢州市","舟山市","台州市","丽水市"]},
    {"n":"安徽省","c":["合肥市","芜湖市","蚌埠市","淮南市","马鞍山市","淮北市","铜陵市","安庆市","黄山市","滁州市","阜阳市","宿州市","巢湖市","六安市","亳州市","池州市","宣城市"]},
    {"n":"福建省","c":["福州市","厦门市","莆田市","三明市","泉州市","漳州市","南平市","龙岩市","宁德市"]},
    {"n":"江西省","c":["南昌市","景德镇市","萍乡市","九江市","新余市","鹰潭市","赣州市","吉安市","宜春市","抚州市","上饶市"]},
    {"n":"山东省","c":["济南市","青岛市","淄博市","枣庄市","东营市","烟台市","潍坊市","济宁市","泰安市","威海市","日照市","莱芜市","临沂市","德州市","聊城市","滨州市","荷泽市"]},
    {"n":"河南省","c":["郑州市","开封市","洛阳市","平顶山市","安阳市","鹤壁市","新乡市","焦作市","濮阳市","许昌市","漯河市","三门峡市","南阳市","商丘市","信阳市","周口市","驻马店市"]},
    {"n":"湖北省","c":["武汉市","黄石市","十堰市","宜昌市","襄樊市","鄂州市","荆门市","孝感市","荆州市","黄冈市","咸宁市","随州市","恩施土家族苗族自治州","仙桃市","潜江市","天门市","神农架林区"]},
    {"n":"湖南省","c":["长沙市","株洲市","湘潭市","衡阳市","邵阳市","岳阳市","常德市","张家界市","益阳市","郴州市","永州市","怀化市","娄底市","湘西土家族苗族自治州"]},
    {"n":"广东省","c":["广州市","深圳市","珠海市","汕头市","韶关市","佛山市","江门市","湛江市","茂名市","肇庆市","惠州市","梅州市","汕尾市","河源市","阳江市","清远市","东莞市","中山市","潮州市","揭阳市","云浮市"]},
    {"n":"甘肃省","c":["兰州市","金昌市","白银市","天水市","嘉峪关市","武威市","张掖市","平凉市","酒泉市","庆阳市","定西市","陇南市","临夏回族自治州","甘南藏族自治州"]},
    {"n":"四川省","c":["成都市","自贡市","攀枝花市","泸州市","德阳市","绵阳市","广元市","遂宁市","内江市","乐山市","南充市","眉山市","宜宾市","广安市","达州市","雅安市","巴中市","资阳市","阿坝藏族羌族自治州","甘孜藏族自治州","凉山彝族自治州"]},
    {"n":"贵州省","c":["贵阳市","六盘水市","遵义市","安顺市","铜仁地区","毕节地区","黔西南布依族苗族自治州","黔东南苗族侗族自治州","黔南布依族苗族自治州"]},
    {"n":"海南省","c":["海口市","三亚市","五指山市","琼海市","儋州市","文昌市","万宁市","东方市","澄迈县","定安县","屯昌县","临高县","白沙黎族自治县","昌江黎族自治县","乐东黎族自治县","陵水黎族自治县","保亭黎族苗族自治县","琼中黎族苗族自治县"]},
    {"n":"云南省","c":["昆明市","曲靖市","玉溪市","保山市","昭通市","丽江市","思茅市","临沧市","楚雄彝族自治州","红河哈尼族彝族自治州","文山壮族苗族自治州","西双版纳傣族自治州","大理白族自治州","德宏傣族景颇族自治州","怒江傈僳族自治州","迪庆藏族自治州"]},
    {"n":"青海省","c":["西宁市","海东地区","海北藏族自治州","黄南藏族自治州","海南藏族自治州","果洛藏族自治州","玉树藏族自治州","海西蒙古族藏族自治州"]},
    {"n":"陕西省","c":["西安市","铜川市","宝鸡市","咸阳市","渭南市","延安市","汉中市","榆林市","安康市","商洛市"]},
    {"n":"广西壮族自治区","c":["南宁市","柳州市","桂林市","梧州市","北海市","防城港市","钦州市","贵港市","玉林市","百色市","贺州市","河池市","来宾市","崇左市"]},
    {"n":"西藏自治区","c":["拉萨市","昌都地区","山南地区","日喀则地区","那曲地区","阿里地区","林芝地区"]},
    {"n":"宁夏回族自治区","c":["银川市","石嘴山市","吴忠市","固原市","中卫市"]}];

$.initProv = function(prov, city, defaultProv, defaultCity) {
    var provEl = $(prov);
    var cityEl = $(city);
    var hasDefaultProv = (typeof(defaultCity) != 'undefined');

    var provHtml = '';

    provHtml += '<option value="-1">请选择</option>';
    for(var i = 0; i < $._cityInfo.length; i++) {
        provHtml += '<option pro="'+$._cityInfo[i].n+'" value="' + i + '"' + ((hasDefaultProv && $._cityInfo[i].n == defaultProv) ? ' selected="selected"' : '') + '>' + $._cityInfo[i].n + '</option>';
    }
    provEl.html(provHtml);
    $.initCities(provEl, cityEl, defaultCity);
    provEl.change(function() {
        $.initCities(provEl, cityEl);
    });
};
$.initCities = function(provEl, cityEl, defaultCity) {
    var hasDefaultCity = (typeof(defaultCity) != 'undefined');
    if(provEl.val() != '' && parseInt(provEl.val()) >= 0) {
        var cities = $._cityInfo[parseInt(provEl.val())].c;
        var cityHtml = '';

        cityHtml += '<option value="-1">请选择</option>';
        for(var i = 0; i < cities.length; i++) {
            cityHtml += '<option value="' + cities[i] + '"' + ((hasDefaultCity && cities[i] == defaultCity) ? ' selected="selected"' : '') + '>' + cities[i] + '</option>';
        }
        cityEl.html(cityHtml);
    } else {
        cityEl.html('<option value="-1">请选择</option>');
    }
};






//天气
function weather() {
    $.ajax({
        type: "POST",
        url: "https://free-api.heweather.com/v5/forecast",
        data: "key=314d013dcb6943da9ad831d50d835fa4&city="+city,
        dataType: "json",
        success: function(msg){
            weatherArr = msg.HeWeather5[0].daily_forecast;
            w_days = weatherArr.length;
            for(var i=0; i<w_days; i++){
                var num = weatherArr[i].cond.code_d;
                //天气图标
                var today = new Date(new Date().getTime()+(i*1000*60*60*24));
                var wYear = today.getFullYear();
                var wMonth = today.getMonth();
                var wDate = today.getDate();
                $("."+wYear+"-"+wMonth+"-"+wDate+" .weather").empty().append('<img class="weatherImg"  src="/images/weather-icon/'+num+'.png">');
                $("."+wYear+"-"+wMonth+"-"+wDate+" .cityName").empty().append(city+':');
                $("."+wYear+"-"+wMonth+"-"+wDate+" .temperature").empty().append(weatherArr[i].cond.txt_d+" "+weatherArr[i].tmp.min+"℃~"+weatherArr[i].tmp.max+'℃');
            }

        }
    });
}

//显示天气图标
function showeather(){
    for(var i=0; i<w_days; i++){
        var num = weatherArr[i].cond.code_d;
        var today = new Date(new Date().getTime()+(i*1000*60*60*24));
        var wYear = today.getFullYear();
        var wMonth = today.getMonth();
        var wDate = today.getDate();
        $("."+wYear+"-"+wMonth+"-"+wDate+" .weather").empty().append('<img class="weatherImg"  src="/images/weather-icon/'+num+'.png">');
        $("."+wYear+"-"+wMonth+"-"+wDate+" .cityName").empty().append(city+':');
        $("."+wYear+"-"+wMonth+"-"+wDate+" .temperature").empty().append(weatherArr[i].cond.txt_d+" "+weatherArr[i].tmp.min+"℃~"+weatherArr[i].tmp.max+'℃');
    }
}