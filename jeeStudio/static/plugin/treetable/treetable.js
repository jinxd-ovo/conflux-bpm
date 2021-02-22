/**JS树表组件**/
(function(a) {
	window.SITE_URL = window.SITE_URL || "";
	if (document.location.href.indexOf("http://") != 0) {
		var b = "../js/treeTable/"
	} else {
		var b = SITE_URL + "/js/treeTable/"
	}
	a.fn.treeTable = function(c) {
		c = a.extend({
			theme: "default",
			expandLevel: 1,
			column: 0,
			url: "",
			callback: function(i) {},
			onSelect: function(i, u) {},
			beforeClose: function(i, u) {},
			beforeExpand: function(i, u) {},
			afterExpand: function(i, u) {},
			beforeClick: function(i, u) {}
		}, c);
		var t = this;
		t.addClass("tree_table");
		var k = {
			"N": c.theme + "_node",
			"AN": c.theme + "_active_node",
			"O": c.theme + "_open",
			"LOAD": c.theme + "_loading",
			"LO": c.theme + "_last_open",
			"S": c.theme + "_shut",
			"LS": c.theme + "_last_shut",
			"HO": c.theme + "_hover_open",
			"HS": c.theme + "_hover_shut",
			"HLO": c.theme + "_hover_last_open",
			"HLS": c.theme + "_hover_last_shut",
			"L": c.theme + "_leaf",
			"LL": c.theme + "_last_leaf",
			"B": c.theme + "_blank",
			"V": c.theme + "_vertline"
		};
		var q = {},
			f = {};
		var d = t.find("tr");
		g(d, true);
		
		t.queryParams = c.queryParams;
		
		t.off("click").off("mouseover").off("mouseout");
		t.click(function(v) {
			var i = a(v.target);
			if (i.attr("controller")) {
				i = i.parents("tr[haschild]").find("[arrow]");
				if (i.attr("class").indexOf(k["AN"]) == -1 && i.attr("class").indexOf(k["N"]) == -1) {
					return
				}
				var w = i.parents("tr[haschild]")[0].id;
				if (c.onSelect && c.onSelect(t, w) === false) {
					return
				}
			}
			if (i.attr("arrow")) {
				var u = i.attr("class");
				if (u == k["AN"] + " " + k["HLO"] || u == k["AN"] + " " + k["HO"]) {
					var w = i.parents("tr[haschild]")[0].id;
					i.attr("class", k["AN"] + " " + (u.indexOf(k["HO"]) != -1 ? k["HS"] : k["HLS"]));
					c.beforeClose(t, w);
					n(w);
					return
				} else {
					if (u == k["AN"] + " " + k["HLS"] || u == k["AN"] + " " + k["HS"]) {
						var w = i.parents("tr")[0].id;
						i.attr("class", k["AN"] + " " + (u.indexOf(k["HS"]) != -1 ? k["HO"] : k["HLO"]));
						c.beforeClick(t, w);
						c.beforeExpand(t, w);
						j(w);
						c.afterExpand(t, w);
						return
					}
				}
			}
		});
		t.expand = function(u) {
			u = u.find("span[class='prev_span']").next();
			if (u.attr("controller")) {
				u = u.parents("tr[haschild]").find("[arrow]");
				if (u.attr("class").indexOf(k["AN"]) == -1 && u.attr("class").indexOf(k["N"]) == -1) {
					return
				}
				var w = u.parents("tr[haschild]")[0].id;
				if (c.onSelect && c.onSelect(t, w) === false) {
					return
				}
			}
			if (u.attr("arrow")) {
				var v = u.attr("class");
				if (v && !v.indexOf(k["AN"])) {
					var i = c.theme.length + 1;
					v = v.split(" ")[1].substr(i);
					if (v.indexOf("hover_") === 0) {
						v = c.theme + "_" + v.substr(6)
					} else {
						v = c.theme + "_hover_" + v
					}
					u.attr("class", k["AN"] + " " + v)
				}
				v = u.attr("class");
				if (v == k["AN"] + " " + k["HLS"] || v == k["AN"] + " " + k["HS"]) {
					var w = u.parents("tr")[0].id;
					u.attr("class", k["AN"] + " " + (v.indexOf(k["HS"]) != -1 ? k["HO"] : k["HLO"]));
					c.beforeExpand(t, w);
					j(w)
				}
			}
			if (u.attr("controller")) {
				u = u.parents("tr[haschild]").find("[arrow]")
			}
			if (u.attr("arrow")) {
				var v = u.attr("class");
				if (v && !v.indexOf(k["AN"])) {
					var i = c.theme.length + 1;
					v = v.split(" ")[1].substr(i);
					if (v.indexOf("hover_") === 0) {
						v = c.theme + "_" + v.substr(6)
					} else {
						v = c.theme + "_hover_" + v
					}
					u.attr("class", k["AN"] + " " + v);
					return
				}
			}
		};
		t.mouseover(m).mouseout(m);

		function m(w) {
			var u = a(w.target);
			if (u.attr("controller")) {
				u = u.parents("tr[haschild]").find("[arrow]")
			}
			if (u.attr("arrow")) {
				var v = u.attr("class");
				if (v && !v.indexOf(k["AN"])) {
					var i = c.theme.length + 1;
					v = v.split(" ")[1].substr(i);
					if (v.indexOf("hover_") === 0) {
						v = c.theme + "_" + v.substr(6)
					} else {
						v = c.theme + "_hover_" + v
					}
					u.attr("class", k["AN"] + " " + v);
					return
				}
			}
		}
		t.initMap = function(i) {
			q = [];
			f = [];
			i.each(function(v) {
				var u = a(this).attr("pId") || 0;
				q[u] || (q[u] = []);
				q[u].push(this.id);
				f[this.id] = u;
				a(this).addClass(u)
			}).find("[controller]").css("cursor", "pointer")
		};
		t.initChild = function(u, i) {
			u.each(function(y) {
				if (!this.id) {
					return
				}
				var A = a(this);
				A.removeAttr("hasChild");
				A.removeAttr("isFirstOne");
				A.removeAttr("isLastOne");
				A.removeAttr("depth");
				if (A.find("span[class='prev_span']").get(0)) {
					A.find("span[class='prev_span']").next().remove();
					A.find("span[class='prev_span']").remove()
				}
				if (A.find("input[type='checkbox']").get(0)) {
					A.find("input[type='checkbox']").remove()
				}
				q[this.id] && A.attr("hasChild", true);
				var w = q[f[this.id]];
				if (w[0] == this.id) {
					A.attr("isFirstOne", true)
				} else {
					var C = 0;
					for (var y = 0; y < w.length; y++) {
						if (w[y] == this.id) {
							break
						}
						C = w[y]
					}
					A.attr("prevId", C)
				}
				w[w.length - 1] == this.id && A.attr("isLastOne", true);
				var B = v(this.id);
				A.attr("depth", B);
				r(this);
				if (i) {
					//wy
					//B > c.expandLevel && A.hide();
					if (A.attr("hasChild") && A.attr("depth") < c.expandLevel) {
						var z = A.attr("isLastOne") ? k["LO"] : k["O"];
						A.find("." + k["AN"]).attr("class", k["AN"] + " " + z)
					}
				}
			});

			function v(y) {
				if (f[y] == 0) {
					return 1
				}
				var w = v(f[y]);
				return w + 1
			}
		};

		function g(u, i) {
			u.each(function(y) {
				var w = a(this).attr("pId") || 0;
				q[w] || (q[w] = []);
				q[w].push(this.id);
				f[this.id] = w;
				a(this).addClass(w)
			}).find("[controller]").css("cursor", "pointer");
			u.each(function(y) {
				if (!this.id) {
					return
				}
				var A = a(this);
				q[this.id] && A.attr("hasChild", true);
				var w = q[f[this.id]];
				if (w[0] == this.id) {
					A.attr("isFirstOne", true)
				} else {
					var C = 0;
					for (var y = 0; y < w.length; y++) {
						if (w[y] == this.id) {
							break
						}
						C = w[y]
					}
					A.attr("prevId", C)
				}
				w[w.length - 1] == this.id && A.attr("isLastOne", true);
				var B = v(this.id);
				A.attr("depth", B);
				r(this);
				if (i) {
					B > c.expandLevel && A.hide();
					if (A.attr("hasChild") && A.attr("depth") < c.expandLevel) {
						var z = A.attr("isLastOne") ? k["LO"] : k["O"];
						A.find("." + k["AN"]).attr("class", k["AN"] + " " + z)
					}
				}
			});

			function v(y) {
				if (f[y] == 0) {
					return 1
				}
				var w = v(f[y]);
				return w + 1
			}
		}
		t.initRelation = g;

		function p(v) {
			t.find("tr[id='" + v + "']").attr("isopen", "false");
			if (!q[v]) {
				return false
			}
			for (var u = 0; u < q[v].length; u++) {
				p(q[v][u])
			}
			a("tr." + v, t).remove()
		}
		t.delChildren = p;

		function l(i) {
			if (!q[i]) {
				return []
			}
			return q[i]
		}
		t.getChildren = l;

		function e(i) {
			return t.find("tr[id='" + i + "']")
		}
		t.get = e;

		function s(i) {
			return t.get(i).attr("pId")
		}
		t.getParentId = s;

		function o(i) {
			t.delChildren(i);
			t.find("tr[id='" + i + "']").remove()
		}
		t.del = o;

		function n(v) {
			t.find("tr[id='" + v + "']").attr("isopen", "false");
			if (!q[v]) {
				return false
			}
			for (var u = 0; u < q[v].length; u++) {
				n(q[v][u])
			}
			a("tr." + v, t).hide()
		}
		t.shut = n;

		function j(y) {
			t.find("tr[id='" + y + "']").attr("isopen", "true");
			a("tr." + y, t).show();
			if (!q[y]) {
				return false
			}
			for (var v = 0; v < q[y].length; v++) {
				var u = q[y][v];
				if (q[u]) {
					var w = a("#" + u, t).find("." + k["AN"]).attr("class");
					(w == k["AN"] + " " + k["O"] || w == k["AN"] + " " + k["LO"]) && j(u)
				}
			}
		}
		t.open = j;

		function r(v) {
			var i = a(v);
			var A = v.id;
			if (f[A] == 0) {
				var y = a('<span class="prev_span"></span>')
			} else {
				if (!i.attr("isFirstOne")) {
					var y = a("#" + i.attr("prevId"), t).children("td").eq(c.column).find(".prev_span").clone()
				} else {
					var w = a("#" + f[A], t);
					var y = w.children("td").eq(c.column).find(".prev_span").clone();
					if (w.attr("isLastOne")) {
						y.append('<span class="' + k["N"] + " " + k["B"] + '"></span>')
					} else {
						y.append('<span class="' + k["N"] + " " + k["V"] + '"></span>')
					}
				}
			}
			if (i.attr("hasChild")) {
				var u = i.attr("isLastOne") ? k["LS"] : k["S"];
				u = k["AN"] + " " + u
			} else {
				var u = k["N"] + " " + (i.attr("isLastOne") ? k["LL"] : k["L"])
			}
			var z = i.children("td").eq(c.column);
			if (c.checkbox) {
				z.prepend('<input type="checkbox" id="' + A + '">')
			}
			z.prepend('<span arrow="true" class="' + u + '"></span>').prepend(y)
		}
		t.addChilds = function(w) {
			var i = a(w);
			if (!i.length) {
				return false
			}
			var v = a(i[0]).attr("pId");
			if (!v) {
				a(t).append(i);
				g(i);
				return false
			}
			var u = v;
			while (q[u]) {
				u = q[u][q[u].length - 1] || u
			}
			if (a("#" + u, t).get(0)) {
				a("#" + u, t).after(i)
			} else {
				a(t).append(i)
			}
			g(i)
		};
		t.refreshPoint = function(i,noParent) {
			var pId = i;
			var param = t.queryParams();
			param.parent = {id:pId};
			if(i===0 || i===-1){
				param = t.queryParams();
				pId = 0;
			}
			if (i != null || i != "") {
				a.ajax({
					url: c.url,
					data: JSON.stringify(param),
					type: "post",
					cache: false,
					async: false,
            		contentType: "application/json",
					dateType: "json",
					headers: {token: localStorage.getItem('$tokenBPM')},
					error: function(u, v) {
						alert("服务器连接失败，请稍候重试！");
						success = false
					},
					success: function(u) {
						if(u.token){
		                	localStorage.setItem("$tokenBPM",u.token);
		                }
						t.initMap(t.find("tr"));
						//wy修改
						if(typeof c.setData == "function"){
							u = c.setData(u,pId);
						}
						a.each(u, function(v, z) {
							var A = z.hasChildren == true;
							if (t.find("tr[id='" + z.id + "']").length == 0) {
								var w = "";
								if(noParent === true){
									w = '<tr id="' + z.id + '" sort="' + z.sort + '" isopen="false"' + (A ? "haschild='true'" : "") + ">"
								}
								else{
									//wy添加
									if(c.sync === false){
										pId = z.parentId;
									}
									
									if (i != -1 ) {
										w = '<tr id="' + z.id + '" sort="' + z.sort + '" isopen="false" pId="' + pId + '" ' + (A ? "haschild='true'" : "") + ">"
									} else {
										w = '<tr id="' + z.id + '" sort="' + z.sort + '" isopen="false"' + (A ? "haschild='true'" : "") + ">"
									}
								}
								t.addChilds(w + c.callback(z) + "</tr>")
								
								//wy添加
								if(c.sync === false){
									noParent = false;
								}
							} else {
								var y = t.find("tr[id='" + z.id + "']");
								y.removeAttr("hasChild");
								y.removeAttr("isFirstOne");
								y.removeAttr("isLastOne");
								y.removeAttr("depth");
								y.html(c.callback(z));
								if (A) {
									y.attr("haschild", "true")
								}
								g(y);
								if (y.attr("isopen") == "true") {
									t.expand(y)
								}
							}
							var y = t.find("tr[id='" + i + "']");
							t.initMap(t.find("tbody").parents("table").find("tr"));
							t.initChild(y, true);
							t.open(y.attr("pId"));
							t.expand(y);
						})
					}
				})
			}
		};
		t.initParents = function(u, y) {
			var w = u.split(",");
			var A = new Array();
			for (var v = w.length - 1; v >= 0; v--) {
				var z = w[v];
				if (z == undefined || z == "") {
					continue
				}
				if (t.get(z).attr("id") && t.get(z).attr("isopen") == "true" || z == y) {
					if (A.length == 0) {
						if (z == y) {
							t.refreshPoint(-1)
						} else {
							t.refreshPoint(z)
						}
					} else {
						A = A.reverse();
						for (x in A) {
							t.refreshPoint(A[x])
						}
					}
					break
				} else {
					if (!t.get(z).attr("id") || t.get(z).attr("isopen") == "false") {
						A.push(z)
					}
				}
			}
		};
		t.refresh = function(data) {
			data = $.extend(true, data, t.queryParams());
			var i = -1;
			a.ajax({
				url: c.url,
				data: JSON.stringify(data),
				type: "post",
				cache: false,
				async: false,
				dateType: "json",
        		contentType: "application/json",
				headers: {token: localStorage.getItem('$tokenBPM')},
				error: function(u, v) {
					alert("服务器连接失败，请稍候重试！");
					success = false
				},
				success: function(u) {
					if(u.token){
	                	localStorage.setItem("$tokenBPM",u.token);
	                }
					//wy修改
					if(typeof c.setData == "function"){
						u = c.setData(u,0);
					}
					
					t.find("tbody").html("");
					a.each(u, function(w, z) {
						var A = z.hasChildren == true;
						var y = '<tr id="' + z.id + '" sort="' + z.sort + '" isopen="false"' + (A ? "haschild='true'" : "") + ">";
						t.addChilds(y + c.callback(z) + "</tr>")
					});
					if (c.expandLevel > 1 && c.url != "") {
						for (var v = 1; v < c.expandLevel; v++) {
							d = t.find("tr[depth='" + v + "']");
							d.each(function(y) {
								var w = this.id;
								if (w != null && w != "") {
									t.refreshPoint(w)
								}
							})
						}
					}
				}
			})
		};
		t.refreshById = function(i,data) {
			a.ajax({
				url: c.url + i,
				data: data,
				type: "post",
				cache: false,
				async: false,
				dateType: "json",
				headers: {token: localStorage.getItem('$tokenBPM')},
				error: function(u, v) {
					alert("服务器连接失败，请稍候重试！");
					success = false
				},
				success: function(u) {
					if(u.token){
	                	localStorage.setItem("$tokenBPM",u.token);
	                }
					t.find("tbody").html("");
					a.each(u, function(w, z) {
						var A = z.hasChildren == true;
						var y = '<tr id="' + z.id + '" sort="' + z.sort + '" isopen="false"' + (A ? "haschild='true'" : "") + ">";
						t.addChilds(y + c.callback(z) + "</tr>")
					});
					if (c.expandLevel > 1 && c.url != "") {
						for (var v = 1; v < c.expandLevel; v++) {
							d = t.find("tr[depth='" + v + "']");
							d.each(function(y) {
								var w = this.id;
								if (w != null && w != "") {
									t.refreshPoint(w)
								}
							})
						}
					}
				}
			})
		};
		t.refreshSelf = function(u) {
			var v = u.hasChildren == true;
			var i = t.find("tr[id='" + u.id + "']");
			i.removeAttr("hasChild");
			i.removeAttr("isFirstOne");
			i.removeAttr("isLastOne");
			i.removeAttr("depth");
			i.html(c.callback(u));
			if (v) {
				i.attr("haschild", "true")
			}
			g(i);
			if (i.attr("isopen") == "true") {
				t.expand(i)
			}
		};
		if (c.expandLevel > 0 && c.url != "" && t.find("tr[depth='1']").length == 0) {
			if(c.sync === false){
				t.refreshPoint(0, true)
			}else{
				t.refreshPoint(-1)
			}
		}
		if (c.expandLevel > 1 && c.url != "") {
			for (var h = 1; h < c.expandLevel; h++) {
				d = t.find("tr[depth='" + h + "']");
				d.each(function(v) {
					var u = this.id;
					if (u != null && u != "") {
						t.refreshPoint(u)
					}
				})
			}
		}
		return t
	}
})(jQuery);