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

angular.module('activitiModeler')
    .controller('ToolbarController', ['$scope', '$http', '$modal', '$q', '$rootScope', '$translate', '$location', function ($scope, $http, $modal, $q, $rootScope, $translate, $location) {

    	$scope.title = "";
    	$scope.saveTitle = "";
    	$scope.editorFactory.promise.then(function () {
    		$scope.items = KISBPM.TOOLBAR_CONFIG.items;
    	});
    	$scope.array = new Array();
    	$scope.commone = "";
    	$scope.num = 0;
        
        $scope.secondaryItems = KISBPM.TOOLBAR_CONFIG.secondaryItems;

        // Call configurable click handler (From http://stackoverflow.com/questions/359788/how-to-execute-a-javascript-function-when-i-have-its-name-as-a-string)
        var executeFunctionByName = function(functionName, context /*, args */) {
            var args = Array.prototype.slice.call(arguments).splice(2);
            var namespaces = functionName.split(".");
            var func = namespaces.pop();
            for(var i = 0; i < namespaces.length; i++) {
                context = context[namespaces[i]];
            }
            return context[func].apply(this, args);
        };

        var jiaohuanArray = function(childShapesNew, newchildShapes, gate){
        	var arrays = new Array();
        	var newchildShapesindex = 0;
        	var newchild = new Array();
        	var l = newchildShapes.length;
        	var index = 0;
        	var newchilel = 0;
        	var childl = childShapesNew.length;
        	for(var n=0;n<childl;n++){
        		var c = childShapesNew[n];
        		if(c.resourceId == gate){
        			index = n;
        			break;
        		}
        	}
        	for(var i=0;i<=index;i++){
        		newchild[i] = childShapesNew[i];
        	}
        	for(var j=index+1;j<=l+index;j++){
        		if(childShapesNew.indexOf(newchildShapes[newchildShapesindex]) == -1){
            		newchild[j] = newchildShapes[newchildShapesindex];
            		newchildShapesindex++;
            		newchilel = j;
            		var ll = arrays.lengeth;
            		if(ll == 0){
            			ll = 0;
            		}else{
            			ll = ll-1;
            		}
            		arrays[ll] = newchildShapes[newchildShapesindex];
        		}else{
        			$scope.commone = newchildShapes[newchildShapesindex];
        			break;
        		}
        	}
        	$scope.array[$scope.num] = arrays;
        	for(var k=index+1;k<childl;k++){
        		if(childShapesNew[k] != undefined){
            		newchilel++;
            		newchild[newchilel] = childShapesNew[k];
        		}
        	}
        	return newchild;
        };
        
        // Click handler for toolbar buttons
        $scope.toolbarButtonClicked = function(buttonIndex) {
            // Default behaviour
            var buttonClicked = $scope.items[buttonIndex];
            $scope.saveTitle = buttonClicked.title;
            if($scope.saveTitle == "TOOLBAR.ACTION.SAVE"){
                $scope.title = "调整";
            }
            if($scope.saveTitle == "TOOLBAR.ACTION.TZ"){
            	tiaozheng();
            }else{
            	var services = { '$scope' : $scope, '$rootScope' : $rootScope, '$http' : $http, '$modal' : $modal, '$q' : $q, '$translate' : $translate};
                
                executeFunctionByName(buttonClicked.action, window, services);

                // Other events
                var event = {
                    type : KISBPM.eventBus.EVENT_TYPE_TOOLBAR_BUTTON_CLICKED,
                    toolbarItem : buttonClicked
                };
                KISBPM.eventBus.dispatch(event.type, event);
            }
        };
        
        var tiaozheng = function(){
        	var modelMetaData = $scope.editor.getModelMetaData();
        	/*console.log(modelMetaData);
        	var description = '';
            if (modelMetaData.description) {
            	description = modelMetaData.description;
            }
        	var saveDialog = { 'name' : modelMetaData.name,
                    'description' : description};
        	var json = $scope.editor.getJSON();*/
        	/*var params = {
		            json_xml: json,
		            svg_xml: svgDOM,
		            name: $scope.saveDialog.name,
		            description: $scope.saveDialog.description  
		        };
		        $scope.saveDialog = saveDialog;
		    */
            var title = $scope.title;
            save();
            $http({ 
    		    method :'POST', 
    		    url : KISBPM.URL.putTz(modelMetaData.modelId)
    		}).success(function(data,status,headers,config) {
		        window.location.reload(); 
    		}).error(function(data,status,headers,config){ 
    		      alert(status);
    		});
           /* if(title != ""){*/
            	/*}else{
            	alert("请先保存");
            }*/
        };
        
        var save = function (successCallback) {
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
            
            // Update
            $http({    method: 'PUT',
                data: params,
                ignoreErrors: true,
                headers: {'Accept': 'application/json',
                          'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                transformRequest: function (obj) {
                    var str = [];
                    for (var p in obj) {
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    }
                    return str.join("&");
                },
                url: KISBPM.URL.putModel(modelMetaData.modelId)})

                .success(function (data, status, headers, config) {
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

                })
                .error(function (data, status, headers, config) {
                    $scope.error = {};
                    //console.log('Something went wrong when updating the process model:' + JSON.stringify(data));
                    $scope.status.loading = false;
                });
        };
        
        // Click handler for secondary toolbar buttons
        $scope.toolbarSecondaryButtonClicked = function(buttonIndex) {
            var buttonClicked = $scope.secondaryItems[buttonIndex];
            var services = { '$scope' : $scope, '$http' : $http, '$modal' : $modal, '$q' : $q, '$translate' : $translate, '$location': $location};
            executeFunctionByName(buttonClicked.action, window, services);
        };
        
        /* Key bindings */
        Mousetrap.bind(['command+z', 'ctrl+z'], function(e) {
        	var services = { '$scope' : $scope, '$rootScope' : $rootScope, '$http' : $http, '$modal' : $modal, '$q' : $q, '$translate' : $translate};
        	KISBPM.TOOLBAR.ACTIONS.undo(services);
            return false;
        });
        
        Mousetrap.bind(['command+y', 'ctrl+y'], function(e) {
        	var services = { '$scope' : $scope, '$rootScope' : $rootScope, '$http' : $http, '$modal' : $modal, '$q' : $q, '$translate' : $translate};
        	KISBPM.TOOLBAR.ACTIONS.redo(services);
            return false;
        });
        
        Mousetrap.bind(['command+c', 'ctrl+c'], function(e) {
        	var services = { '$scope' : $scope, '$rootScope' : $rootScope, '$http' : $http, '$modal' : $modal, '$q' : $q, '$translate' : $translate};
        	KISBPM.TOOLBAR.ACTIONS.copy(services);
            return false;
        });
        
        Mousetrap.bind(['command+v', 'ctrl+v'], function(e) {
        	var services = { '$scope' : $scope, '$rootScope' : $rootScope, '$http' : $http, '$modal' : $modal, '$q' : $q, '$translate' : $translate};
        	KISBPM.TOOLBAR.ACTIONS.paste(services);
            return false;
        });
        
        Mousetrap.bind(['del'], function(e) {
        	var services = { '$scope' : $scope, '$rootScope' : $rootScope, '$http' : $http, '$modal' : $modal, '$q' : $q, '$translate' : $translate};
        	KISBPM.TOOLBAR.ACTIONS.deleteItem(services);
            return false;
        });

        /* Undo logic */

        $scope.undoStack = [];
        $scope.redoStack = [];

        $scope.editorFactory.promise.then(function() {

            // Catch all command that are executed and store them on the respective stacks
            $scope.editor.registerOnEvent(ORYX.CONFIG.EVENT_EXECUTE_COMMANDS, function( evt ){

                // If the event has commands
                if( !evt.commands ){ return; }

                $scope.undoStack.push( evt.commands );
                $scope.redoStack = [];
                
                for(var i = 0; i < $scope.items.length; i++) 
        		{
                    var item = $scope.items[i];
                    if (item.action === 'KISBPM.TOOLBAR.ACTIONS.undo')
                    {
                    	item.enabled = true;
                    }
                    else if (item.action === 'KISBPM.TOOLBAR.ACTIONS.redo')
                    {
                    	item.enabled = false;
                    }
        		}

                // Update
                $scope.editor.getCanvas().update();
                $scope.editor.updateSelection();

            });

        });
        
        // Handle enable/disable toolbar buttons 
        $scope.editorFactory.promise.then(function() {
        	$scope.editor.registerOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, function( evt ){
        		var elements = evt.elements;
        		
        		for(var i = 0; i < $scope.items.length; i++) 
        		{
                    var item = $scope.items[i];
                    if (item.enabledAction && item.enabledAction === 'element')
                    {
                    	var minLength = 1;
                    	if(item.minSelectionCount) {
                    		minLength = item.minSelectionCount;
                    	}
                    	if (elements.length >= minLength && !item.enabled) {
                    		$scope.safeApply(function () {
                    			item.enabled = true;
                            });
                    	}
                    	else if (elements.length == 0 && item.enabled) {
                    		$scope.safeApply(function () {
                    			item.enabled = false;
                            });
                    	}
                    }
                }
        	});
        	
        });

    }]);