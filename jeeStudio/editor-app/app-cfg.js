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
var host_url = 'http://127.0.0.1:8900/jeeStudio/gtoa/a';
var ACTIVITI = ACTIVITI || {};
var pathName = window.document.location.pathname;
var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
ACTIVITI.CONFIG = {
	'contextRoot' : host_url + '/service',
	'contextBaseRoot' : host_url
};
ACTIVITI.AUTO = {
	'contextAuto' : host_url + '/a/act/model'
};
