
layui.define(['echarts', 'admin'], function (exports) {
    var $ = layui.$
        , echarts = layui.echarts
        , admin = layui.admin
        , chart = {

            initEchpiebar: function (options) {

                var legend = [], xData = [];
                $.each(options.data, function (idx, data) {
                    if (data.legend && $.inArray(data.legend, legend) === -1)
                        legend.push(data.legend);
                    if ($.inArray(data['X' + admin.lang()] ? data['X' + admin.lang()] : data.X, xData) === -1)
                        xData.push(data['X' + admin.lang()] ? data['X' + admin.lang()] : data.X);
                });
                var legendLength = legend.length;
                if (legendLength === 0)
                    legend.push(options.legend || 'all');

                var seriesData = [];
                $.each(legend, function (yk, yv) {
                    $.each(xData, function (xk, xv) {
                        var dataItem = { name: xv, value: '' };
                        $.each(options.data, function (idx, item) {
                            if (legendLength > 0) {
                                if ((item['X' + admin.lang()] ? item['X' + admin.lang()] : item.X) === xv && item.legend === yv) {
                                    dataItem.value = item.Y;
                                    return false;
                                }
                            } else {
                                if ((item['X' + admin.lang()] ? item['X' + admin.lang()] : item.X) === xv) {
                                    dataItem.value = item.Y;
                                    return false;
                                }

                            }
                        });
                        seriesData.push(dataItem);

                    });
                });
                
                var echpiebar = [],
                    piebar = [{
                        tooltip: {
                            trigger: 'item',
                            formatter: "{b}: {c} ({d}%)"
                        },
                        avoidLabelOverlap: false,
                        legend: {
                            show:false,
                            data: xData
                        },
                        series: [
                            {
                                type: 'pie',
                                radius: ['60%', '90%'],
                                avoidLabelOverlap: false,
                                label: {
                                    normal: {
                                        show: false,
                                        position: 'center'
                                    },
                                    emphasis: {
                                        show: true,
                                        textStyle: {
                                            fontWeight: 'bold'
                                        }
                                    }
                                },
                                labelLine: {
                                    normal: {
                                        show: false
                                    }
                                },
                                data: seriesData
                            }
                        ]
                    }],
                    elemEchpiebar = $(options.elem),
                    renderEchpiebar = function (index) {
                        echpiebar[index] = echarts.init(elemEchpiebar[index], layui.echartsTheme);
                        echpiebar[index].setOption(piebar[0]);
                        window.onresize = echpiebar[index].resize;
                    };
                if (!elemEchpiebar[0]) return;
                renderEchpiebar(0);
            }

             ,initHeapline: function (options) {

                var legend = [], xData = [];
                $.each(options.data, function (idx, data) {
                    if (data.legend && $.inArray(data.legend, legend) === -1)
                        legend.push(data.legend);
                    if ($.inArray(data['X' + admin.lang()] ? data['X' + admin.lang()] : data.X, xData) === -1)
                        xData.push(data['X' + admin.lang()] ? data['X' + admin.lang()] : data.X);
                });
                var legendLength = legend.length;
                if (legendLength === 0)
                    legend.push(options.legend || 'all');

                var series = [];
                $.each(legend, function (yk, yv) {
                    var item = {
                        name: yv
                        , type: 'line'
                    };
                    var data = [];
                    $.each(xData, function (xk, xv) {
                        var dataItem = '';
                        $.each(options.data, function (idx, item) {
                            if (legendLength > 0) {
                                if ((item['X' + admin.lang()] ? item['X' + admin.lang()] : item.X) === xv && item.legend === yv) {
                                    dataItem = item.Y;
                                    return false;
                                }
                            } else {
                                if ((item['X' + admin.lang()] ? item['X' + admin.lang()] : item.X) === xv) {
                                    dataItem = item.Y;
                                    return false;
                                }

                            }
                        });
                        data.push(dataItem);

                    });
                    item.data = data;
                    series.push(item);
                });
                //堆积折线图
                var echheapline = []
                    , heapline = [
                        {
                            tooltip: {
                                trigger: 'axis'
                            },
                            legend: {
                                show: false,
                                data: legend
                            },
                            calculable: true,
                            grid: {
                                x: 30,
                                y: 10,
                                x2: 10,
                                y2: 30,
                                borderWidth: 0
                            },
                            xAxis: [
                                {
                                    type: 'category',
                                    boundaryGap: false,
                                    data: xData
                                }
                            ],
                            yAxis: [
                                {
                                    type: 'value'
                                }
                            ],
                            series: series
                        }
                    ]
                    , elemheapline = $(options.elem)
                    , renderheapline = function (index) {
                        echheapline[index] = echarts.init(elemheapline[index], layui.echartsTheme);
                        echheapline[index].setOption(heapline[index]);
                        window.onresize = echheapline[index].resize;
                    };
                if (!elemheapline[0]) return;
                renderheapline(0);
            }

            , initHeaparea: function (options) {

                var legend = [], xData = [];
                $.each(options.data, function (idx, data) {
                    if (data.legend && $.inArray(data.legend, legend) === -1)
                        legend.push(data.legend);
                    if ($.inArray(data['X' + admin.lang()] ? data['X' + admin.lang()] : data.X, xData) === -1)
                        xData.push(data['X' + admin.lang()] ? data['X' + admin.lang()] : data.X);
                });
                var legendLength = legend.length;
                if (legendLength === 0)
                    legend.push(options.legend || 'all');

                var series = [];
                $.each(legend, function (yk, yv) {
                    var item = {
                        name: yv
                        , type: 'line'
                        , stack: 'all'
                        , itemStyle: { normal: { areaStyle: { type: 'default' } } }
                    };
                    var data = [];
                    $.each(xData, function (xk, xv) {
                        var dataItem = '';
                        $.each(options.data, function (idx, item) {
                            if (legendLength > 0) {
                                if ((item['X' + admin.lang()] ? item['X' + admin.lang()] : item.X) === xv && item.legend === yv) {
                                    dataItem = item.Y;
                                    return false;
                                }
                            } else {
                                if ((item['X' + admin.lang()] ? item['X' + admin.lang()] : item.X) === xv) {
                                    dataItem = item.Y;
                                    return false;
                                }

                            }
                        });
                        data.push(dataItem);

                    });
                    item.data = data;
                    series.push(item);
                });
                //堆积折线图
                var echheaparea = []
                    , heaparea = [
                        {
                            tooltip: {
                                trigger: 'axis'
                                //, axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                                //    type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                                //}
                            },
                            legend: {
                                show: false,
                                data: legend
                            },
                            calculable: true,
                            grid: {
                                x: 30,
                                y: 10,
                                x2: 10,
                                y2: 30,
                                borderWidth: 0
                            },
                            xAxis: [
                                {
                                    type: 'category',
                                    boundaryGap: false,
                                    data: xData
                                }
                            ],
                            yAxis: [
                                {
                                    type: 'value'
                                }
                            ],
                            series: series
                        }
                    ]
                    , elemHeaparea = $(options.elem)
                    , renderHeaparea = function (index) {
                        echheaparea[index] = echarts.init(elemHeaparea[index], layui.echartsTheme);
                        echheaparea[index].setOption(heaparea[index]);
                        window.onresize = echheaparea[index].resize;
                    };
                if (!elemHeaparea[0]) return;
                renderHeaparea(0);
            }

            , initEchnormcol: function (options) {
                var legend = [], xData = [];
                $.each(options.data, function (idx, data) {
                    if (data.legend && $.inArray(data.legend, legend) === -1)
                        legend.push(data.legend);
                    if ($.inArray(data['X' + admin.lang()] ? data['X' + admin.lang()] : data.X, xData) === -1)
                        xData.push(data['X' + admin.lang()] ? data['X' + admin.lang()] : data.X);
                });
                var legendLength = legend.length;
                if (legendLength === 0)
                    legend.push(options.legend || 'all');

                var series = [];
                $.each(legend, function (yk, yv) {
                    var item = {
                        name: yv
                        , type: 'bar'
                        , markPoint: {
                            data: [{
                                type: 'max',
                                name: admin.lang() ? 'max' : '最大值'
                            },
                            {
                                type: 'min',
                                name: admin.lang() ? 'min' : '最小值'
                            }
                            ]
                        }
                        , markLine: {
                            data: [{
                                type: 'average',
                                name: admin.lang() ? 'average' : '平均值'
                            }]
                        }
                    };
                    var data = [];
                    $.each(xData, function (xk, xv) {
                        var dataItem = '';
                        $.each(options.data, function (idx, item) {
                            if (legendLength > 0) {
                                if ((item['X' + admin.lang()] ? item['X' + admin.lang()] : item.X) === xv && item.legend === yv) {
                                    dataItem = item.Y;
                                    return false;
                                }
                            } else {
                                if ((item['X' + admin.lang()] ? item['X' + admin.lang()] : item.X) === xv) {
                                    dataItem = item.Y;
                                    return false;
                                }

                            }
                        });
                        data.push(dataItem);

                    });
                    item.data = data;
                    series.push(item);
                });
                var echnormcol = [], normcol = [
                    {

                        tooltip: {
                            trigger: 'axis'
                            , axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                                type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                            }
                        },
                        legend: {
                            show: false,
                            data: legend
                        },
                        calculable: true,
                        xAxis: [
                            {
                                type: 'category',
                                data: xData
                            }
                        ],
                        yAxis: [
                            {
                                type: 'value'
                            }
                        ],
                        series: series
                    }
                ]
                    , elemEchnormcol = $(options.elem)
                    , renderEchnormcol = function (index) {
                        echnormcol[index] = echarts.init(elemEchnormcol[index], layui.echartsTheme);
                        echnormcol[index].setOption(normcol[index]);
                        window.onresize = echnormcol[index].resize;
                    };
                if (!elemEchnormcol[0]) return;
                renderEchnormcol(0);
            }

            , initEchnormbar: function (options) {
                var legend = [], xData = [];
                $.each(options.data, function (idx, data) {
                    if (data.legend && $.inArray(data.legend, legend) === -1)
                        legend.push(data.legend);
                    if ($.inArray(data['X' + admin.lang()] ? data['X' + admin.lang()] : data.X, xData) === -1)
                        xData.push(data['X' + admin.lang()] ? data['X' + admin.lang()] : data.X);
                });
                var legendLength = legend.length;
                if (legendLength === 0)
                    legend.push(options.legend || 'all');

                var series = [];
                $.each(legend, function (yk, yv) {
                    var item = {
                        name: yv
                        , type: 'bar'
                    };
                    var data = [];
                    $.each(xData, function (xk, xv) {
                        var dataItem = '';
                        $.each(options.data, function (idx, item) {
                            if (legendLength > 0) {
                                if ((item['X' + admin.lang()] ? item['X' + admin.lang()] : item.X) === xv && item.legend === yv) {
                                    dataItem = item.Y;
                                    return false;
                                }
                            } else {
                                if ((item['X' + admin.lang()] ? item['X' + admin.lang()] : item.X) === xv) {
                                    dataItem = item.Y;
                                    return false;
                                }

                            }
                        });
                        data.push(dataItem);

                    });
                    item.data = data;
                    series.push(item);
                });

                var echnormbar = []
                    , normbar = [
                        {
                            tooltip: {
                                trigger: 'axis'
                                , axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                                    type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                                }
                            },
                            legend: {
                                show: false,
                                data: legend
                            },
                            calculable: true,
                            grid: {
                                x: 0,
                                y: 0,
                                x2: 0,
                                y2: 0,
                                borderWidth: 0
                            },
                            xAxis: [
                                {
                                    type: 'value'
                                    , boundaryGap: [0, 0.01]
                                }
                            ],
                            yAxis: [
                                {
                                    type: 'category',
                                    data: xData
                                }
                            ],
                            series: series
                        }
                ]
                    , elemNormbar = $(options.elem)
                    , rendeNormbar = function (index) {
                        echnormbar[index] = echarts.init(elemNormbar[index], layui.echartsTheme);
                        echnormbar[index].setOption(normbar[index]);
                        window.onresize = echnormbar[index].resize;
                    };
                if (!elemNormbar[0]) return;
                rendeNormbar(0);
            }
        };

    exports('report', chart);
});