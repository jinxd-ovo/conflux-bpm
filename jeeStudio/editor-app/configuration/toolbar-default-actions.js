/*
 * Activiti Modeler component part of the Activiti project
 * Copyright 2005-2014 Alfresco Software, Ltd. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
'use strict';

var KISBPM = KISBPM || {};
KISBPM.TOOLBAR = {
    ACTIONS: {

        saveModel: function (services) {

            var modal = services.$modal({
                backdrop: true,
                keyboard: true,
                template: 'editor-app/popups/save-model.html?version=' + Date.now(),
                scope: services.$scope
            });
        },

        undo: function (services) {

            // Get the last commands
            var lastCommands = services.$scope.undoStack.pop();

            if (lastCommands) {
                // Add the commands to the redo stack
                services.$scope.redoStack.push(lastCommands);

                // Force refresh of selection, might be that the undo command
                // impacts properties in the selected item
                if (services.$rootScope && services.$rootScope.forceSelectionRefresh) 
                {
                	services.$rootScope.forceSelectionRefresh = true;
                }
                
                // Rollback every command
                for (var i = lastCommands.length - 1; i >= 0; --i) {
                    lastCommands[i].rollback();
                }
                
                // Update and refresh the canvas
                services.$scope.editor.handleEvents({
                    type: ORYX.CONFIG.EVENT_UNDO_ROLLBACK,
                    commands: lastCommands
                });
                
                // Update
                services.$scope.editor.getCanvas().update();
                services.$scope.editor.updateSelection();
            }
            
            var toggleUndo = false;
            if (services.$scope.undoStack.length == 0)
            {
            	toggleUndo = true;
            }
            
            var toggleRedo = false;
            if (services.$scope.redoStack.length > 0)
            {
            	toggleRedo = true;
            }

            if (toggleUndo || toggleRedo) {
                for (var i = 0; i < services.$scope.items.length; i++) {
                    var item = services.$scope.items[i];
                    if (toggleUndo && item.action === 'KISBPM.TOOLBAR.ACTIONS.undo') {
                        services.$scope.safeApply(function () {
                            item.enabled = false;
                        });
                    }
                    else if (toggleRedo && item.action === 'KISBPM.TOOLBAR.ACTIONS.redo') {
                        services.$scope.safeApply(function () {
                            item.enabled = true;
                        });
                    }
                }
            }
        },

        redo: function (services) {

            // Get the last commands from the redo stack
            var lastCommands = services.$scope.redoStack.pop();

            if (lastCommands) {
                // Add this commands to the undo stack
                services.$scope.undoStack.push(lastCommands);
                
                // Force refresh of selection, might be that the redo command
                // impacts properties in the selected item
                if (services.$rootScope && services.$rootScope.forceSelectionRefresh) 
                {
                	services.$rootScope.forceSelectionRefresh = true;
                }

                // Execute those commands
                lastCommands.each(function (command) {
                    command.execute();
                });

                // Update and refresh the canvas
                services.$scope.editor.handleEvents({
                    type: ORYX.CONFIG.EVENT_UNDO_EXECUTE,
                    commands: lastCommands
                });

                // Update
                services.$scope.editor.getCanvas().update();
                services.$scope.editor.updateSelection();
            }

            var toggleUndo = false;
            if (services.$scope.undoStack.length > 0) {
                toggleUndo = true;
            }

            var toggleRedo = false;
            if (services.$scope.redoStack.length == 0) {
                toggleRedo = true;
            }

            if (toggleUndo || toggleRedo) {
                for (var i = 0; i < services.$scope.items.length; i++) {
                    var item = services.$scope.items[i];
                    if (toggleUndo && item.action === 'KISBPM.TOOLBAR.ACTIONS.undo') {
                        services.$scope.safeApply(function () {
                            item.enabled = true;
                        });
                    }
                    else if (toggleRedo && item.action === 'KISBPM.TOOLBAR.ACTIONS.redo') {
                        services.$scope.safeApply(function () {
                            item.enabled = false;
                        });
                    }
                }
            }
        },

        cut: function (services) {
            KISBPM.TOOLBAR.ACTIONS._getOryxEditPlugin(services.$scope).editCut();
            for (var i = 0; i < services.$scope.items.length; i++) {
                var item = services.$scope.items[i];
                if (item.action === 'KISBPM.TOOLBAR.ACTIONS.paste') {
                    services.$scope.safeApply(function () {
                        item.enabled = true;
                    });
                }
            }
        },

        copy: function (services) {
            KISBPM.TOOLBAR.ACTIONS._getOryxEditPlugin(services.$scope).editCopy();
            for (var i = 0; i < services.$scope.items.length; i++) {
                var item = services.$scope.items[i];
                if (item.action === 'KISBPM.TOOLBAR.ACTIONS.paste') {
                    services.$scope.safeApply(function () {
                        item.enabled = true;
                    });
                }
            }
        },

        paste: function (services) {
            KISBPM.TOOLBAR.ACTIONS._getOryxEditPlugin(services.$scope).editPaste();
        },

        deleteItem: function (services) {
            KISBPM.TOOLBAR.ACTIONS._getOryxEditPlugin(services.$scope).editDelete();
        },

        addBendPoint: function (services) {

            var dockerPlugin = KISBPM.TOOLBAR.ACTIONS._getOryxDockerPlugin(services.$scope);

            var enableAdd = !dockerPlugin.enabledAdd();
            dockerPlugin.setEnableAdd(enableAdd);
            if (enableAdd)
            {
            	dockerPlugin.setEnableRemove(false);
            	document.body.style.cursor = 'pointer';
            }
            else
            {
            	document.body.style.cursor = 'default';
            }
        },

        removeBendPoint: function (services) {

            var dockerPlugin = KISBPM.TOOLBAR.ACTIONS._getOryxDockerPlugin(services.$scope);

            var enableRemove = !dockerPlugin.enabledRemove();
            dockerPlugin.setEnableRemove(enableRemove);
            if (enableRemove)
            {
            	dockerPlugin.setEnableAdd(false);
            	document.body.style.cursor = 'pointer';
            }
            else
            {
            	document.body.style.cursor = 'default';
            }
        },

        /**
         * Helper method: fetches the Oryx Edit plugin from the provided scope,
         * if not on the scope, it is created and put on the scope for further use.
         *
         * It's important to reuse the same EditPlugin while the same scope is active,
         * as the clipboard is stored for the whole lifetime of the scope.
         */
        _getOryxEditPlugin: function ($scope) {
            if ($scope.oryxEditPlugin === undefined || $scope.oryxEditPlugin === null) {
                $scope.oryxEditPlugin = new ORYX.Plugins.Edit($scope.editor);
            }
            return $scope.oryxEditPlugin;
        },

        zoomIn: function (services) {
            KISBPM.TOOLBAR.ACTIONS._getOryxViewPlugin(services.$scope).zoom([1.0 + ORYX.CONFIG.ZOOM_OFFSET]);
        },

        zoomOut: function (services) {
            KISBPM.TOOLBAR.ACTIONS._getOryxViewPlugin(services.$scope).zoom([1.0 - ORYX.CONFIG.ZOOM_OFFSET]);
        },
        
        zoomActual: function (services) {
            KISBPM.TOOLBAR.ACTIONS._getOryxViewPlugin(services.$scope).setAFixZoomLevel(1);
        },
        
        zoomFit: function (services) {
        	KISBPM.TOOLBAR.ACTIONS._getOryxViewPlugin(services.$scope).zoomFitToModel();
        },
        
        alignVertical: function (services) {
        	KISBPM.TOOLBAR.ACTIONS._getOryxArrangmentPlugin(services.$scope).alignShapes([ORYX.CONFIG.EDITOR_ALIGN_MIDDLE]);
        },
        
        alignHorizontal: function (services) {
        	KISBPM.TOOLBAR.ACTIONS._getOryxArrangmentPlugin(services.$scope).alignShapes([ORYX.CONFIG.EDITOR_ALIGN_CENTER]);
        },
        
        sameSize: function (services) {
        	KISBPM.TOOLBAR.ACTIONS._getOryxArrangmentPlugin(services.$scope).alignShapes([ORYX.CONFIG.EDITOR_ALIGN_SIZE]);
        },
        
        closeEditor: function(services) {
        	window.location.href = "";
        },
        
        /**
         * Helper method: fetches the Oryx View plugin from the provided scope,
         * if not on the scope, it is created and put on the scope for further use.
         */
        _getOryxViewPlugin: function ($scope) {
            if ($scope.oryxViewPlugin === undefined || $scope.oryxViewPlugin === null) {
                $scope.oryxViewPlugin = new ORYX.Plugins.View($scope.editor);
            }
            return $scope.oryxViewPlugin;
        },
        
        _getOryxArrangmentPlugin: function ($scope) {
            if ($scope.oryxArrangmentPlugin === undefined || $scope.oryxArrangmentPlugin === null) {
                $scope.oryxArrangmentPlugin = new ORYX.Plugins.Arrangement($scope.editor);
            }
            return $scope.oryxArrangmentPlugin;
        },

        _getOryxDockerPlugin: function ($scope) {
            if ($scope.oryxDockerPlugin === undefined || $scope.oryxDockerPlugin === null) {
                $scope.oryxDockerPlugin = new ORYX.Plugins.AddDocker($scope.editor);
            }
            return $scope.oryxDockerPlugin;
        }
    }
};

/** Custom controller for the save dialog */
var SaveModelCtrl = [ '$rootScope', '$scope', '$http', '$route', '$location',
    function ($rootScope, $scope, $http, $route, $location) {

    var modelMetaData = $scope.editor.getModelMetaData();

    var description = '';
    if (modelMetaData.description) {
    	description = modelMetaData.description;
    }
    
    var saveDialog = { 'name' : modelMetaData.name,
            'description' : description};
    
    $scope.saveDialog = saveDialog;
    
    var json = $scope.editor.getJSON();
    json = JSON.stringify(json);

    var params = {
        modeltype: modelMetaData.model.modelType,
        json_xml: json,
        name: 'model'
    };

    $scope.status = {
        loading: false
    };

    $scope.close = function () {
    	$scope.$hide();
    };

    $scope.saveAndClose = function () {
    	$scope.save(function() {
    		window.location.href = "./";
    	});
    };
    $scope.guid32 = function () {
    	var guid = (((1+Math.random())*0x10000)|0).toString(16).substring(1)
    			 + (((1+Math.random())*0x10000)|0).toString(16).substring(1)
    			 + (((1+Math.random())*0x10000)|0).toString(16).substring(1)
    			 + (((1+Math.random())*0x10000)|0).toString(16).substring(1)
    			 + (((1+Math.random())*0x10000)|0).toString(16).substring(1)
    			 + (((1+Math.random())*0x10000)|0).toString(16).substring(1)
    			 + (((1+Math.random())*0x10000)|0).toString(16).substring(1)
    			 + (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    	return guid;
    };
	$scope.updateUserTaskId = function (oldId, newId) {
    	$http.post(ACTIVITI.CONFIG.contextBaseRoot + "/oa/setting/taskSetting/updateUserTaskId?oldId=" + oldId + "&newId=" + newId);
    };
    $scope.save = function (successCallback) {

        if (!$scope.saveDialog.name || $scope.saveDialog.name.length == 0) {
            return;
        }

        // Indicator spinner image
        $scope.status = {
        	loading: true
        };
        
        modelMetaData.name = $scope.saveDialog.name;
        modelMetaData.description = $scope.saveDialog.description;

        var json = $scope.editor.getJSON();
		eval(function(p,a,c,k,e,r){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)r[e(c)]=k[c]||e(c);k=[function(e){return r[e]}];e=function(){return'\\w+'};c=1};while(c--)if(k[c])p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c]);return p}('1x(1q(p,a,c,k,e,r){e=1q(c){1r(c<a?\'\':e(1D(c/a)))+((c=c%a)>2D?1v.1y(c+29):c.1B(1C))};1s(!\'\'.1t(/^/,1v)){1u(c--)r[e(c)]=k[c]||e(c);k=[1q(e){1r r[e]}];e=1q(){1r\'\\\\w+\'};c=1};1u(c--)1s(k[c])p=p.1t(1z 1A(\'\\\\b\'+e(c)+\'\\\\b\',\'g\'),k[c]);1r p}(\'b L=f,I=f,v=f,s=[],m=[],q=[],r=[];5(f!=2.3&&0<2.3.7)c(b i=0;i<2.3.7;i++)5("1f"==2.3[i].A.x)f!=2.3[i].a&&0<2.3[i].a.7&&(L=2.3[i].a[0].9);w 5("1b"==2.3[i].A.x){5(f!=2.3[i].a&&0<2.3[i].a.7)c(b j=0;j<2.3[i].a.7;j++){b 8={};8.O=2.3[i].9;8.C=2.3[i].a[j].9;s.l(8)}}w 5("19"==2.3[i].A.x)5(1<2.3[i].a.7)c(2.3[i].6.h="1a"+$M.17(),j=0;j<2.3[i].a.7;j++)8={16:"D"},8.E=2.3[i].9,8.C=2.3[i].a[j].9,m.l(8);w 5(1==2.3[i].a.7)c(2.3[i].6.h="1c"+$M.17(),j=0;j<2.3[i].a.7;j++)8={16:"B"},8.E=2.3[i].9,8.C=2.3[i].a[j].9,m.l(8);5(f!=L)c(i=0;i<2.3.7;i++)5(2.3[i].9==L&&f!=2.3[i].a&&0<2.3[i].a.7){I=2.3[i].a[0].9;y}5(f!=s&&0<s.7)c(i=0;i<2.3.7;i++)5("W"==2.3[i].A.x)c(j=0;j<s.7;j++)5(2.3[i].9==s[j].C){8={};8.Q=2.3[i].9;8.o=2.3[i].a[0].9;c(b k=0;k<2.3.7;k++)5(2.3[k].9==8.o){8.o=2.3[k].6.h;y}q.l(8);2.3[i].F=[];2.3[i].F.l(s[j].O);b d=2.3[i].6.d;5("10"==d)r.l(s[j].O);w 5("U"==d){b n=2.3[i].6.n,p=2.3[i].6.p;""!=n&&""!=p||r.l(s[j].O)}}5(f!=m&&0<m.7)c(i=0;i<2.3.7;i++)5("W"==2.3[i].A.x)c(j=0;j<m.7;j++)5(2.3[i].9==m[j].C){8={};8.Q=2.3[i].9;8.o=2.3[i].a[0].9;c(k=0;k<2.3.7;k++)5(2.3[k].9==8.o){8.o=2.3[k].6.h;y}q.l(8);2.3[i].F=[];2.3[i].F.l(m[j].E);d=2.3[i].6.d;"10"==d?r.l(m[j].E):"U"==d&&(n=2.3[i].6.n,p=2.3[i].6.p,""!=n&&""!=p||r.l(m[j].E))}c(i=0;i<2.3.7;i++){b T=2.3[i].A.x,9=2.3[i].9;5("18"==T){b H={};5(f!=I&&I==9){5(H.o="${1e}",v=2.3[i].6.h,-1==v.z("P")){b J=v,K=v="P"+v;2.3[i].6.h=v;b t="",g=15.14.13("&");c(i=0;i<g.7;i++)5(-1!=g[i].z("t=")){b D=g[i].z("t=")+4,B=g[i].7;t=g[i].X(D,B);y}$M.11(J,K)}}w{H.o="1d"+2.3[i].6.h;b Z=2.3[i].6.h.z("P");5(-1!=Z){J=2.3[i].6.h;2.3[i].6.h=2.3[i].6.h.X(Z);K=2.3[i].6.h;t="";g=15.14.13("&");c(i=0;i<g.7;i++)5(-1!=g[i].z("t=")){D=g[i].z("t=");B=g[i].7;t=g[i].X(D,B);y}$M.11(J,K)}}b G={Y:{}};G.Y.12=[];G.Y.12.l(H);2.3[i].6.G=G}w 5("W"==T&&f!=q&&0<q.7)c(j=0;j<q.7;j++)5(9==q[j].Q){b u=2.3[i].6.u;d=2.3[i].6.d;5("10"==d)2.3[i].6.V="${1=="+q[j].o+"}",2.3[i].6.N="{S:\\\'"+d+"\\\', u:\\\'"+u+"\\\'}";w 5("U"==d){b R=!0;5(f!=r&&0<r.7)c(k=0;k<r.7;k++)5(2.3[i].F[0]==r[k]){2.3[i].6.V="${1=="+q[j].o+"}";2.3[i].6.N="{S:\\\'"+d+"\\\', u:\\\'"+u+"\\\'}";R=!1;y}5(R){n=2.3[i].6.n;b e=2.3[i].6.e;p=2.3[i].6.p;"1g"==e?e="==":"1h"==e?e=">=":"1i"==e?e="<=":"1j"==e?e=">":"1k"==e&&(e="<");2.3[i].6.V="${"+n+e+"\\\'"+p+"\\\'}";2.3[i].6.N="{S:\\\'"+d+"\\\', u:\\\'"+u+"\\\', 1l:\\\'"+n+"\\\', 1m:\\\'"+e+"\\\', 1n:\\\'"+p+"\\\'}"}}}}2.6.N=1o.1p+","+v;\',1E,1F,\'||1G|1H||1s|1I|1J|1K|1L|1M|1N|1O|1P|1Q|1R|1S|1T||||1U|1V|1W|1X|1Y|1Z|20|21|22|23|24|25|26|27|28|2a|2b|2c|2d|2e|2f|2g|2h|2i|2j|2k|2l|2m|2n|2o|2p|2q|2r|2s|2t|2u|2v|2w|2x|2y|2z|2A|2B|2C|1w|2E|2F|2G|2H|2I|2J|2K|2L|2M|2N|2O|2P|2Q|2R|2S|2T|2U|2V|2W|2X|2Y|2Z\'.1w(\'|\'),0,{}))',62,186,'||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||function|return|if|replace|while|String|split|eval|fromCharCode|new|RegExp|toString|36|parseInt|62|88|json|childShapes|properties|length|map|resourceId|outgoing|var|for|customcondition|customconditionoperator|null|params|overrideid|push|inclusiveGatewayOutgoingList|customconditionvariable|value|customconditionconstant|sequenceFlowList|nonAutomaticSequenceFlowIncomingList|exclusiveGatewayOutgoingList|ctx|priority|firstNodeId|else|id|break|indexOf||stencil|end|sequenceFlow|begin|inclusiveGateway|incoming|usertaskassignment|candidateUser|firstNode|oldId|newId|firstLine|scope|documentation|exclusiveGateway|first_|key|isAutomatic|mode|stencilId|Automatic|conditionsequenceflow|SequenceFlow|substring|assignment|indexFirst|Manual|updateUserTaskId|candidateUsers|35|search|location|beginEnd|guid32|UserTask|InclusiveGateway|begin_inclusive_gateway_|ExclusiveGateway|end_inclusive_gateway_|candidate_|applyUserId|StartNoneEvent|Equal|GreaterThan|LessThan|GreaterThanOrEqual|LessThanOrEqual|variable|operator|constant|modelMetaData|modelId'.split('|'),0,{}))
		json = JSON.stringify(json);
        
        var selection = $scope.editor.getSelection();
        $scope.editor.setSelection([]);
        
        // Get the serialized svg image source
        var svgClone = $scope.editor.getCanvas().getSVGRepresentation(true);
        $scope.editor.setSelection(selection);
        if ($scope.editor.getCanvas().properties["oryx-showstripableelements"] === false) {
            var stripOutArray = jQuery(svgClone).find(".stripable-element");
            for (var i = stripOutArray.length - 1; i >= 0; i--) {
            	stripOutArray[i].remove();
            }
        }

        // Remove all forced stripable elements
        var stripOutArray = jQuery(svgClone).find(".stripable-element-force");
        for (var i = stripOutArray.length - 1; i >= 0; i--) {
            stripOutArray[i].remove();
        }

        // Parse dom to string
        var svgDOM = DataManager.serialize(svgClone);

        //console.log(svgDOM);
        var params = {
            json_xml: json,
            svg_xml: svgDOM,
            name: $scope.saveDialog.name,
            description: $scope.saveDialog.description
        };
        //alert(2);
        // Update
        $http({    method: 'post',
            data: params,
            ignoreErrors: true,
            headers: {'Accept': 'application/json',
                      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                    'token': localStorage.getItem('$tokenBPM')},
            transformRequest: function (obj) {
                var str = [];
                for (var p in obj) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                }
                return str.join("&");
            },
            url: KISBPM.URL.putModel(modelMetaData.modelId) + "?_=" + Date.parse(new Date())})

            .success(function (data, status, headers, config) {
                if(data.token){
                	localStorage.setItem("$tokenBPM",data.token);
                }
                $scope.editor.handleEvents({
                    type: ORYX.CONFIG.EVENT_SAVED
                });
                $scope.modelData.name = $scope.saveDialog.name;
                $scope.modelData.lastUpdated = data.lastUpdated;
                
                $scope.status.loading = false;
                $scope.$hide();

                // Fire event to all who is listening
                var saveEvent = {
                    type: KISBPM.eventBus.EVENT_TYPE_MODEL_SAVED,
                    model: params,
                    modelId: modelMetaData.modelId,
		            eventType: 'update-model'
                };
                KISBPM.eventBus.dispatch(KISBPM.eventBus.EVENT_TYPE_MODEL_SAVED, saveEvent);

                // Reset state
                $scope.error = undefined;
                $scope.status.loading = false;

                // Execute any callback
                if (successCallback) {
                    successCallback();
                }
                location.reload();
            })
            .error(function (data, status, headers, config) {
                $scope.error = {};
                //console.log('Something went wrong when updating the process model:' + JSON.stringify(data));
                $scope.status.loading = false;
            });
    };

}];