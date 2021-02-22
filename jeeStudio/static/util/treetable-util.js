;
(function(global) {
	"use strict";
	var treetableUtil = function(getTime) {
		var _this = this;
		_this.tableId = "#table-list" + getTime;
		_this.dom = $("#table-list" + getTime);
		_this.body = $("#body-div" + getTime);
		_this.$treeTable = "";
		_this.config = {
			theme: 'vsStyle',
			expandLevel: 1,
			column: 0,
			checkbox: false,
			beforeClick: function($treeTable, id) {
				$treeTable.refreshPoint(id)
			},
			beforeExpand: function($treeTable, id) {},
			afterExpand: function($treeTable, id) {},
			beforeClose: function($treeTable, id) {},
			afterClose: function($treeTable, id) {},
			sync: true,
			queryParams: function(param) {
				var data = myHelper.composeData(_this.body.find("#searchForm"));
				return data;
			},
			setData: function(res, pId) {
				var list = res.rows;
				$.each(list, function(idx, obj) {
					obj.parentId = obj.parent.id;
					obj.isopen = true
				});
				var data = [];
				if(this.sync === false) {
					data = $.grep(list, function(obj, idx) {
						return obj.parentId == pId
					});
					var callFuc = function(father) {
						$.each(father, function(i, item) {
							var children = $.grep(list, function(obj, idx) {
								return obj.parentId == item.id
							});
							data = data.concat(children);
							if(children.length > 0) {
								callFuc.call(callFuc, children)
							}
						})
					};
					callFuc(data)
				} else {
					data = $.grep(list, function(obj, idx) {
						return obj.parentId == pId
					})
				}
				return data
			}
		}
	};
	treetableUtil.prototype = {
		init: function(option, listDictionaryId, formDictionaryId) {
			this.bindFunction();
			this.appendToolBtns();
			this.setBtnLang();
			this.setSearchFormLang(formDictionaryId || listDictionaryId);
			if(option.thead) {
				layui.admin.getDictionary('page-table-cell', listDictionaryId, function(data) {
					if(data && data.length > 0) {
						$.each(option.thead, function(idx, item) {
							if($.isPlainObject(item)) {
								if(item.field) {
									var find = layui.admin.filterList(data, {
										member: item.field
									})[0];
									if(find) {
										item.title = find['memberName' + layui.admin.lang()] || item.title
									}
								}
							}
						})
					}
				});
				var html = "<thead><tr>";
				$.each(option.thead, function(idx, item) {
					if(item) {
						if(item.width) {
							html += '<th class="center" style="width: 45px;">'
						} else {
							html += "<th>"
						}
						html += item.title + "</th>"
					}
				});
				html += "</tr></thead>";
				this.dom.append(html);
				var _this = this;
				if(option.thead[option.thead.length - 1].events) {
					$.each(option.thead[option.thead.length - 1].events, function(keys, fuc) {
						var event = keys.split(" ")[0];
						var selector = keys.split(" ")[1];
						$(document).on(event, _this.tableId + " " + selector, function() {
							var row = JSON.parse($(this).closest("td").attr("data-json"));
							fuc(row)
						})
					})
				}
			}
			var totalConfig = $.extend(true, {}, this.config, option);
			if(totalConfig.sync === false) {
				delete totalConfig.beforeClick
			}
			this.$treeTable = this.dom.treeTable(totalConfig);
			this.dom.addClass("table table-hover bootstrap-table tree_table");
			return this.$treeTable
		},
		setSearchFormLang: function(dictionaryId) {
			var _this = this;
			myHelper.setFormLang(dictionaryId, _this.body.find("#searchForm"))
		},
		setBtnLang: function() {
			var _this = this;
			if(layui.admin.lang()) {
				_this.body.find("#toolbar").find("#searchBtn font").text("Search");
				_this.body.find("#toolbar").find("#resetBtn font").text("Reset");
				_this.body.find("#toolbar").find("#easySearch font").text("Back");
				_this.body.find("#toolbar").find("#hardSearch font").text("Advanced Query");
				_this.body.find(".searchDiv.positionSpc").css("right", "300px")
			}
		},
		appendToolBtns: function() {
			var _this = this;
			_this.body.find("#toolbar").append(LayerUtil.getBtnsStrFromMenuData().toolBtnsStr)
		},
		bindFunction: function() {
			var _this = this;
			_this.body.find("#searchBtn").on("click", function() {
				_this.refresh()
			});
			_this.body.find("#resetBtn").on("click", function() {
				_this.body.find("#searchForm input[type='text']").val("");
				_this.body.find("#searchForm input[type='hidden']").val("");
				_this.body.find("#searchForm select[component-type='select']").val("").trigger("change");
				_this.refresh()
			})
		},
		setBackground: function() {
			$(".my-table-striped>tbody>tr").css("background-color", "transparent");
			$(".my-table-striped>tbody>tr:visible:odd").css("background-color", "#f9f9f9")
		},
		refresh: function() {
			this.$treeTable.refresh({})
		},
		refreshPoint: function(id) {
			this.$treeTable.refreshPoint(id)
		},
		getTarget: function(id) {
			return this.$treeTable.get(id)
		},
		del:function(id){
			this.$treeTable.del(id)
		},
		initParents:function(ids){
			this.$treeTable.initParents(ids,"0");
		},
	};
	if(typeof module !== 'undefined' && module.exports) {
		module.exports = treetableUtil
	}
	if(typeof define === 'function') {
		define(function() {
			return treetableUtil
		})
	}
	global.TreetableUtil = treetableUtil;
})(this);