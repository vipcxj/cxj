(function(window) {

    if(window.PrimeFaces) {
        window.PrimeFaces.debug("PrimeFaces already loaded, ignoring duplicate execution.");
        return;
    }

    var PrimeFaces = {

        escapeClientId : function(id) {
            return "#" + id.replace(/:/g,"\\:");
        },

        cleanWatermarks : function(){
            $.watermark.hideAll();
        },

        showWatermarks : function(){
            $.watermark.showAll();
        },

        getWidgetById : function(id) {

            for (var widgetVar in PrimeFaces.widgets) {
                var widget = PrimeFaces.widgets[widgetVar];
                if (widget && widget.id === id) {
                    return widget;
                }
            }

            return null;
        },

        addSubmitParam : function(parent, params) {
            var form = $(this.escapeClientId(parent));

            for(var key in params) {
                form.append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + params[key] + "\" class=\"ui-submit-param\"/>");
            }

            return this;
        },

        /**
         * Submits a form and clears ui-submit-param after that to prevent dom caching issues
         */
        submit : function(formId, target) {
            var form = $(this.escapeClientId(formId));
            if(target) {
                form.attr('target', target);
            }

            form.submit().children('input.ui-submit-param').remove();
        },

        attachBehaviors : function(element, behaviors) {
            $.each(behaviors, function(event, fn) {
                element.bind(event, function(e) {
                    fn.call(element, e);
                });
            });
        },

        getCookie : function(name) {
            return $.cookie(name);
        },

        setCookie : function(name, value, cfg) {
            $.cookie(name, value, cfg);
        },

        deleteCookie: function(name, cfg) {
            $.removeCookie(name, cfg);
        },

        cookiesEnabled: function() {
            var cookieEnabled = (navigator.cookieEnabled) ? true : false;

            if(typeof navigator.cookieEnabled === 'undefined' && !cookieEnabled) {
                document.cookie="testcookie";
                cookieEnabled = (document.cookie.indexOf("testcookie") !== -1) ? true : false;
            }

            return (cookieEnabled);
        },

        skinInput : function(input) {
            input.hover(
                function() {
                    $(this).addClass('ui-state-hover');
                },
                function() {
                    $(this).removeClass('ui-state-hover');
                }
            ).focus(function() {
                $(this).addClass('ui-state-focus');
            }).blur(function() {
                $(this).removeClass('ui-state-focus');
            });

            //aria
            input.attr('role', 'textbox')
                    .attr('aria-disabled', input.is(':disabled'))
                    .attr('aria-readonly', input.prop('readonly'));

            if(input.is('textarea')) {
                input.attr('aria-multiline', true);
            }

            return this;
        },

        skinButton : function(button) {
            button.mouseover(function(){
                var el = $(this);
                if(!button.prop('disabled')) {
                    el.addClass('ui-state-hover');
                }
            }).mouseout(function() {
                $(this).removeClass('ui-state-active ui-state-hover');
            }).mousedown(function() {
                var el = $(this);
                if(!button.prop('disabled')) {
                    el.addClass('ui-state-active').removeClass('ui-state-hover');
                }
            }).mouseup(function() {
                $(this).removeClass('ui-state-active').addClass('ui-state-hover');
            }).focus(function() {
                $(this).addClass('ui-state-focus');
            }).blur(function() {
                $(this).removeClass('ui-state-focus ui-state-active');
            }).keydown(function(e) {
                if(e.keyCode === $.ui.keyCode.SPACE || e.keyCode === $.ui.keyCode.ENTER || e.keyCode === $.ui.keyCode.NUMPAD_ENTER) {
                    $(this).addClass('ui-state-active');
                }
            }).keyup(function() {
                $(this).removeClass('ui-state-active');
            });

            //aria
            var role = button.attr('role');
            if(!role) {
                button.attr('role', 'button');
            }
            button.attr('aria-disabled', button.prop('disabled'));

            return this;
        },

        skinSelect : function(select) {
            select.mouseover(function() {
                var el = $(this);
                if(!el.hasClass('ui-state-focus'))
                    el.addClass('ui-state-hover');
            }).mouseout(function() {
                $(this).removeClass('ui-state-hover');
            }).focus(function() {
                $(this).addClass('ui-state-focus').removeClass('ui-state-hover');
            }).blur(function() {
                $(this).removeClass('ui-state-focus ui-state-hover');
            });

            return this;
        },

        //Deprecated, use PrimeFaces.env.isIE instead
        isIE: function(version) {
            return PrimeFaces.env.isIE(version);
        },

        info: function(log) {
            if(this.logger) {
                this.logger.info(log);
            }
        },

        debug: function(log) {
            if(this.logger) {
                this.logger.debug(log);
            }
        },

        warn: function(log) {
            if(this.logger) {
                this.logger.warn(log);
            }

            if (PrimeFaces.isDevelopmentProjectStage() && window.console) {
                console.log(log);
            }
        },

        error: function(log) {
            if(this.logger) {
                this.logger.error(log);
            }

            if (PrimeFaces.isDevelopmentProjectStage() && window.console) {
                console.log(log);
            }
        },

        isDevelopmentProjectStage: function() {
            return PrimeFaces.settings.projectStage === 'Development';
        },

        setCaretToEnd: function(element) {
            if(element) {
                element.focus();
                var length = element.value.length;

                if(length > 0) {
                    if(element.setSelectionRange) {
                        element.setSelectionRange(0, length);
                    }
                    else if (element.createTextRange) {
                      var range = element.createTextRange();
                      range.collapse(true);
                      range.moveEnd('character', 1);
                      range.moveStart('character', 1);
                      range.select();
                    }
                }
            }
        },

        changeTheme: function(newTheme) {
            if(newTheme && newTheme !== '') {
                var themeLink = $('link[href*="javax.faces.resource/theme.css"]');
                // portlet
                if (themeLink.length === 0) {
                    themeLink = $('link[href*="javax.faces.resource=theme.css"]');
                }

                var themeURL = themeLink.attr('href'),
                    plainURL = themeURL.split('&')[0],
                    oldTheme = plainURL.split('ln=')[1],
                    newThemeURL = themeURL.replace(oldTheme, 'primefaces-' + newTheme);

                themeLink.attr('href', newThemeURL);
            }
        },

        escapeRegExp: function(text) {
            return this.escapeHTML(text.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1"));
        },

        escapeHTML: function(value) {
            return value.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
        },

        clearSelection: function() {
            if(window.getSelection) {
                if(window.getSelection().empty) {
                    window.getSelection().empty();
                } else if(window.getSelection().removeAllRanges) {
                    window.getSelection().removeAllRanges();
                }
            }
            else if(document.selection && document.selection.empty) {
                try {
                    document.selection.empty();
                } catch(error) {
                    //ignore IE bug
                }
            }
        },

        getSelection: function() {
            var text = '';
            if (window.getSelection) {
                text = window.getSelection();
            } else if (document.getSelection) {
                text = document.getSelection();
            } else if (document.selection) {
                text = document.selection.createRange().text;
            }

            return text;
        },

        hasSelection: function() {
            return this.getSelection().length > 0;
        },

        cw : function(widgetConstructor, widgetVar, cfg, resource) {
            PrimeFaces.createWidget(widgetConstructor, widgetVar, cfg, resource);
        },

        createWidget : function(widgetConstructor, widgetVar, cfg, resource) {
            cfg.widgetVar = widgetVar;

            if(PrimeFaces.widget[widgetConstructor]) {
                var widget = PrimeFaces.widgets[widgetVar];

                //ajax update
                if(widget && (widget.constructor === PrimeFaces.widget[widgetConstructor])) {
                    widget.refresh(cfg);
                }
                //page init
                else {
                    PrimeFaces.widgets[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);
                    if(PrimeFaces.settings.legacyWidgetNamespace) {
                        window[widgetVar] = PrimeFaces.widgets[widgetVar];
                    }
                }
            }
            // widget script not loaded -> lazy load script + stylesheet
            else {
                var scriptURI = PrimeFaces.getFacesResource(resource + '/' + resource + '.js', 'primefaces');
                var cssURI = PrimeFaces.getFacesResource(resource + '/' + resource + '.css', 'primefaces');

                //load css
                var cssResource = '<link type="text/css" rel="stylesheet" href="' + cssURI + '" />';
                $('head').append(cssResource);

                //load script and initialize widget
                PrimeFaces.getScript(scriptURI, function() {
                    setTimeout(function() {
                        PrimeFaces.widgets[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);
                    }, 100);
                });
            }
        },

        /**
         * Builds a resource URL for given parameters.
         *
         * @param {string} name The name of the resource. For example: primefaces.js
         * @param {string} library The library of the resource. For example: primefaces
         * @param {string} version The version of the library. For example: 5.1
         * @returns {string} The resource URL.
         */
        getFacesResource : function(name, library, version) {
            var scriptURI = $('script[src*="/javax.faces.resource/' + PrimeFaces.getCoreScriptName() + '"]').attr('src');
            // portlet
            if (!scriptURI) {
                scriptURI = $('script[src*="javax.faces.resource=' + PrimeFaces.getCoreScriptName() + '"]').attr('src');
            }

            scriptURI = scriptURI.replace(PrimeFaces.getCoreScriptName(), name);
            scriptURI = scriptURI.replace('ln=primefaces', 'ln=' + library);

            if (version) {
                var extractedVersion = new RegExp('[?&]v=([^&]*)').exec(scriptURI)[1];
                scriptURI = scriptURI.replace('v=' + extractedVersion, 'v=' + version);
            }

            var prefix = window.location.protocol + '//' + window.location.host;
            return scriptURI.indexOf(prefix) >= 0 ? scriptURI : prefix + scriptURI;
        },

        getCoreScriptName: function() {
            return 'primefaces.js';
        },

        inArray: function(arr, item) {
            for(var i = 0; i < arr.length; i++) {
                if(arr[i] === item) {
                    return true;
                }
            }

            return false;
        },

        isNumber: function(value) {
            return typeof value === 'number' && isFinite(value);
        },

        getScript: function(url, callback) {
            $.ajax({
                type: "GET",
                url: url,
                success: callback,
                dataType: "script",
                cache: true
            });
        },

        focus : function(id, context) {
            var selector = ':not(:submit):not(:button):input:visible:enabled[name]';

            setTimeout(function() {
                if(id) {
                    var jq = $(PrimeFaces.escapeClientId(id));

                    if(jq.is(selector)) {
                        jq.focus();
                    }
                    else {
                        jq.find(selector).eq(0).focus();
                    }
                }
                else if(context) {
                    $(PrimeFaces.escapeClientId(context)).find(selector).eq(0).focus();
                }
                else {
                    $(selector).eq(0).focus();
                }
            }, 250);

            // remember that a custom focus has been rendered
            // this avoids to retain the last focus after ajax update
            PrimeFaces.customFocus = true;
        },

        monitorDownload: function(start, complete) {
            if(this.cookiesEnabled()) {
                if(start) {
                    start();
                }

                window.downloadMonitor = setInterval(function() {
                    var downloadComplete = PrimeFaces.getCookie('primefaces.download');

                    if(downloadComplete === 'true') {
                        if(complete) {
                            complete();
                        }
                        clearInterval(window.downloadMonitor);
                        PrimeFaces.setCookie('primefaces.download', null);
                    }
                }, 250);
            }
        },

        /**
         *  Scrolls to a component with given client id
         */
        scrollTo: function(id) {
            var offset = $(PrimeFaces.escapeClientId(id)).offset();

            $('html,body').animate({
                scrollTop:offset.top
                ,
                scrollLeft:offset.left
            },{
                easing: 'easeInCirc'
            },1000);

        },

        /**
         *  Aligns container scrollbar to keep item in container viewport, algorithm copied from jquery-ui menu widget
         */
        scrollInView: function(container, item) {
            if(item.length === 0) {
                return;
            }

            var borderTop = parseFloat(container.css('borderTopWidth')) || 0,
            paddingTop = parseFloat(container.css('paddingTop')) || 0,
            offset = item.offset().top - container.offset().top - borderTop - paddingTop,
            scroll = container.scrollTop(),
            elementHeight = container.height(),
            itemHeight = item.outerHeight(true);

            if(offset < 0) {
                container.scrollTop(scroll + offset);
            }
            else if((offset + itemHeight) > elementHeight) {
                container.scrollTop(scroll + offset - elementHeight + itemHeight);
            }
        },

        calculateScrollbarWidth: function() {
            if(!this.scrollbarWidth) {
                if(PrimeFaces.env.browser.msie) {
                    var $textarea1 = $('<textarea cols="10" rows="2"></textarea>')
                            .css({ position: 'absolute', top: -1000, left: -1000 }).appendTo('body'),
                        $textarea2 = $('<textarea cols="10" rows="2" style="overflow: hidden;"></textarea>')
                            .css({ position: 'absolute', top: -1000, left: -1000 }).appendTo('body');
                    this.scrollbarWidth = $textarea1.width() - $textarea2.width();
                    $textarea1.add($textarea2).remove();
                }
                else {
                    var $div = $('<div />')
                        .css({ width: 100, height: 100, overflow: 'auto', position: 'absolute', top: -1000, left: -1000 })
                        .prependTo('body').append('<div />').find('div')
                            .css({ width: '100%', height: 200 });
                    this.scrollbarWidth = 100 - $div.width();
                    $div.parent().remove();
                }
            }

            return this.scrollbarWidth;
        },

        bcn: function(element, event, functions) {
            if(functions) {
                for(var i = 0; i < functions.length; i++) {
                    var retVal = functions[i].call(element, event);
                    if(retVal === false) {
                        if(event.preventDefault) {
                            event.preventDefault();
                        }
                        else {
                            event.returnValue = false;
                        }

                        break;
                    }
                }
            }
        },

        bcnu: function(ext, event, fns) {
            if(fns) {
                for(var i = 0; i < fns.length; i++) {
                    var retVal = fns[i].call(ext, event);
                    if(retVal === false) {
                        break;
                    }
                }
            }
        },

    	/**
    	 * moved to core.dialog.js
    	 */
        openDialog: function(cfg) {
        	PrimeFaces.dialog.DialogHandler.openDialog(cfg);
        },
        closeDialog: function(cfg) {
        	PrimeFaces.dialog.DialogHandler.closeDialog(cfg);
        },
        showMessageInDialog: function(msg) {
        	PrimeFaces.dialog.DialogHandler.showMessageInDialog(msg);
        },
        confirm: function(msg) {
        	PrimeFaces.dialog.DialogHandler.confirm(msg);
        },

        deferredRenders: [],

        addDeferredRender: function(widgetId, containerId, fn) {
            this.deferredRenders.push({widget: widgetId, container: containerId, callback: fn});
        },

        removeDeferredRenders: function(widgetId) {
            for(var i = (this.deferredRenders.length - 1); i >= 0; i--) {
                var deferredRender = this.deferredRenders[i];

                if(deferredRender.widget === widgetId) {
                    this.deferredRenders.splice(i, 1);
                }
            }
        },

        invokeDeferredRenders: function(containerId) {
            var widgetsToRemove = [];
            for(var i = 0; i < this.deferredRenders.length; i++) {
                var deferredRender = this.deferredRenders[i];

                if(deferredRender.container === containerId) {
                    var rendered = deferredRender.callback.call();
                    if(rendered) {
                        widgetsToRemove.push(deferredRender.widget);
                    }
                }
            }

            for(var j = 0; j < widgetsToRemove.length; j++) {
                this.removeDeferredRenders(widgetsToRemove[j]);
            }
        },

        zindex : 1000,

        customFocus : false,

        detachedWidgets : [],

        PARTIAL_REQUEST_PARAM : "javax.faces.partial.ajax",

        PARTIAL_UPDATE_PARAM : "javax.faces.partial.render",

        PARTIAL_PROCESS_PARAM : "javax.faces.partial.execute",

        PARTIAL_SOURCE_PARAM : "javax.faces.source",

        BEHAVIOR_EVENT_PARAM : "javax.faces.behavior.event",

        PARTIAL_EVENT_PARAM : "javax.faces.partial.event",

        RESET_VALUES_PARAM : "primefaces.resetvalues",

        IGNORE_AUTO_UPDATE_PARAM : "primefaces.ignoreautoupdate",

        VIEW_STATE : "javax.faces.ViewState",

        CLIENT_WINDOW : "javax.faces.ClientWindow",

        VIEW_ROOT : "javax.faces.ViewRoot",

        CLIENT_ID_DATA : "primefaces.clientid"
    };

    /**
     * PrimeFaces Namespaces
     */
    PrimeFaces.settings = {};
    PrimeFaces.util = {};
    PrimeFaces.widgets = {};

    /**
     * Locales
     */
    PrimeFaces.locales = {

        'en_US': {
            closeText: 'Close',
            prevText: 'Previous',
            nextText: 'Next',
            monthNames: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
            monthNamesShort: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ],
            dayNames: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
            dayNamesShort: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
            dayNamesMin: ['S', 'M', 'T', 'W ', 'T', 'F ', 'S'],
            weekHeader: 'Week',
            firstDay: 0,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix:'',
            timeOnlyTitle: 'Only Time',
            timeText: 'Time',
            hourText: 'Hour',
            minuteText: 'Minute',
            secondText: 'Second',
            currentText: 'Current Date',
            ampm: false,
            month: 'Month',
            week: 'week',
            day: 'Day',
            allDayText: 'All Day'
        }

    };

    PF = function(widgetVar) {
    	var widgetInstance = PrimeFaces.widgets[widgetVar];

    	if (!widgetInstance) {
	        PrimeFaces.error("Widget for var '" + widgetVar + "' not available!");
    	}

        return widgetInstance;
    };

    //expose globally
    window.PrimeFaces = PrimeFaces;

})(window);

PrimeFaces.env = {
    
    mobile : false,
    touch : false,
    ios: false,
    browser : null,
    
    init : function() {
        this.mobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(window.navigator.userAgent);
        this.touch = 'ontouchstart' in window || window.navigator.msMaxTouchPoints || PrimeFaces.env.mobile;
        this.ios = /iPhone|iPad|iPod/i.test(window.navigator.userAgent);
        
        this.resolveUserAgent();
    },
    
    //adapted from jquery browser plugin
    resolveUserAgent: function() {
        if($.browser) {
            this.browser = $.browser;
        }
        else {
            var matched, browser;

            jQuery.uaMatch = function( ua ) {
              ua = ua.toLowerCase();

              var match = /(opr)[\/]([\w.]+)/.exec( ua ) ||
                  /(chrome)[ \/]([\w.]+)/.exec( ua ) ||
                  /(version)[ \/]([\w.]+).*(safari)[ \/]([\w.]+)/.exec( ua ) ||
                  /(webkit)[ \/]([\w.]+)/.exec( ua ) ||
                  /(opera)(?:.*version|)[ \/]([\w.]+)/.exec( ua ) ||
                  /(msie) ([\w.]+)/.exec( ua ) ||
                  ua.indexOf("trident") >= 0 && /(rv)(?::| )([\w.]+)/.exec( ua ) ||
                  ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec( ua ) ||
                  [];

              var platform_match = /(ipad)/.exec( ua ) ||
                  /(iphone)/.exec( ua ) ||
                  /(android)/.exec( ua ) ||
                  /(windows phone)/.exec( ua ) ||
                  /(win)/.exec( ua ) ||
                  /(mac)/.exec( ua ) ||
                  /(linux)/.exec( ua ) ||
                  /(cros)/i.exec( ua ) ||
                  [];

              return {
                  browser: match[ 3 ] || match[ 1 ] || "",
                  version: match[ 2 ] || "0",
                  platform: platform_match[ 0 ] || ""
              };
            };

            matched = jQuery.uaMatch( window.navigator.userAgent );
            browser = {};

            if ( matched.browser ) {
              browser[ matched.browser ] = true;
              browser.version = matched.version;
              browser.versionNumber = parseInt(matched.version);
            }

            if ( matched.platform ) {
              browser[ matched.platform ] = true;
            }

            // These are all considered mobile platforms, meaning they run a mobile browser
            if ( browser.android || browser.ipad || browser.iphone || browser[ "windows phone" ] ) {
              browser.mobile = true;
            }

            // These are all considered desktop platforms, meaning they run a desktop browser
            if ( browser.cros || browser.mac || browser.linux || browser.win ) {
              browser.desktop = true;
            }

            // Chrome, Opera 15+ and Safari are webkit based browsers
            if ( browser.chrome || browser.opr || browser.safari ) {
              browser.webkit = true;
            }

            // IE11 has a new token so we will assign it msie to avoid breaking changes
            if ( browser.rv )
            {
              var ie = "msie";

              matched.browser = ie;
              browser[ie] = true;
            }

            // Opera 15+ are identified as opr
            if ( browser.opr )
            {
              var opera = "opera";

              matched.browser = opera;
              browser[opera] = true;
            }

            // Stock Android browsers are marked as Safari on Android.
            if ( browser.safari && browser.android )
            {
              var android = "android";

              matched.browser = android;
              browser[android] = true;
            }

            // Assign the name and platform variable
            browser.name = matched.browser;
            browser.platform = matched.platform;

            this.browser = browser;
            $.browser = browser;
        }
    },
    
    isIE: function(version) {
        return (version === undefined) ? this.browser.msie: (this.browser.msie && parseInt(this.browser.version, 10) === version);
    },
    
    isLtIE: function(version) {
        return (this.browser.msie) ? parseInt(this.browser.version, 10) < version : false;
    },
    
    isCanvasSupported: function() {
        var elem = document.createElement('canvas');
        return !!(elem.getContext && elem.getContext('2d'));
    }
};

PrimeFaces.env.init();
/**
 * AJAX parameter shortcut mapping for PrimeFaces.ab
 */
PrimeFaces.AB_MAPPING = {
    's': 'source',
    'f': 'formId',
    'p': 'process',
    'u': 'update',
    'e': 'event',
    'a': 'async',
    'g': 'global',
    'd': 'delay',
    't': 'timeout',
    'iau': 'ignoreAutoUpdate',
    'ps': 'partialSubmit',
    'psf': 'partialSubmitFilter',
    'rv': 'resetValues',
    'fi': 'fragmentId',
    'fu': 'fragmentUpdate',
    'pa': 'params',
    'onst': 'onstart',
    'oner': 'onerror',
    'onsu': 'onsuccess',
    'onco': 'oncomplete'
};

/**
 * Ajax shortcut
 */
PrimeFaces.ab = function(cfg, ext) {
    for (var option in cfg) {
        if (!cfg.hasOwnProperty(option)) {
            continue;
        }

        // just pass though if no mapping is available
        if (this.AB_MAPPING[option]) {
            cfg[this.AB_MAPPING[option]] = cfg[option];
            delete cfg[option];
        }
    }

    PrimeFaces.ajax.Request.handle(cfg, ext);
};

PrimeFaces.ajax = {

    VIEW_HEAD : "javax.faces.ViewHead",
    VIEW_BODY : "javax.faces.ViewBody",

    Utils: {

        getContent: function(node) {
            var content = '';

            for(var i = 0; i < node.childNodes.length; i++) {
                content += node.childNodes[i].nodeValue;
            }

            return content;
        },

        updateFormStateInput: function(name, value, xhr) {
            var trimmedValue = $.trim(value);

            var forms = null;
            if (xhr && xhr.pfSettings && xhr.pfSettings.portletForms) {
                forms = $(xhr.pfSettings.portletForms);
            }
            else {
                forms = $('form');
            }

            var parameterNamespace = '';
            if (xhr && xhr.pfArgs && xhr.pfArgs.parameterNamespace) {
                parameterNamespace = xhr.pfArgs.parameterNamespace;
            }

            for (var i = 0; i < forms.length; i++) {
                var form = forms.eq(i);

                if (form.attr('method') === 'post') {
                    var input = form.children("input[name='" + parameterNamespace + name + "']");

                    if (input.length > 0) {
                        input.val(trimmedValue);
                    } else {
                        form.append('<input type="hidden" name="' + parameterNamespace + name + '" value="' + trimmedValue + '" autocomplete="off" />');
                    }
                }
            }
        },

        updateElement: function(id, content, xhr) {
            if (id.indexOf(PrimeFaces.VIEW_STATE) !== -1) {
                PrimeFaces.ajax.Utils.updateFormStateInput(PrimeFaces.VIEW_STATE, content, xhr);
            }
            else if (id.indexOf(PrimeFaces.CLIENT_WINDOW) !== -1) {
                PrimeFaces.ajax.Utils.updateFormStateInput(PrimeFaces.CLIENT_WINDOW, content, xhr);
            }
            else if (id === PrimeFaces.VIEW_ROOT) {
                // reset PrimeFaces JS state
                window.PrimeFaces = null;

                var cache = $.ajaxSetup()['cache'];
                $.ajaxSetup()['cache'] = true;
                $('head').html(content.substring(content.indexOf("<head>") + 6, content.lastIndexOf("</head>")));
                $.ajaxSetup()['cache'] = cache;

                var bodyStartTag = new RegExp("<body[^>]*>", "gi").exec(content)[0];
                var bodyStartIndex = content.indexOf(bodyStartTag) + bodyStartTag.length;
                $('body').html(content.substring(bodyStartIndex, content.lastIndexOf("</body>")));
            }
            else if (id === PrimeFaces.ajax.VIEW_HEAD) {
                // reset PrimeFaces JS state
                window.PrimeFaces = null;

                var cache = $.ajaxSetup()['cache'];
                $.ajaxSetup()['cache'] = true;
                $('head').html(content.substring(content.indexOf("<head>") + 6, content.lastIndexOf("</head>")));
                $.ajaxSetup()['cache'] = cache;
            }
            else if (id === PrimeFaces.ajax.VIEW_BODY) {
                var bodyStartTag = new RegExp("<body[^>]*>", "gi").exec(content)[0];
                var bodyStartIndex = content.indexOf(bodyStartTag) + bodyStartTag.length;
                $('body').html(content.substring(bodyStartIndex, content.lastIndexOf("</body>")));
            }
            else {
                $(PrimeFaces.escapeClientId(id)).replaceWith(content);
            }
        }
    },

    Queue: {

        delays: {},

        requests: new Array(),

        xhrs: new Array(),

        offer: function(request) {
            if(request.delay) {
                var sourceId = null,
                $this = this,
                sourceId = (typeof(request.source) === 'string') ? request.source: $(request.source).attr('id'),
                createTimeout = function() {
                        return setTimeout(function() {
                            $this.requests.push(request);

                            if($this.requests.length === 1) {
                                PrimeFaces.ajax.Request.send(request);
                            }
                        }, request.delay);
                };

                if(this.delays[sourceId]) {
                    clearTimeout(this.delays[sourceId].timeout);
                    this.delays[sourceId].timeout = createTimeout();
                }
                else {
                    this.delays[sourceId] = {
                        timeout: createTimeout()
                    };
                }
            }
            else {
                this.requests.push(request);

                if(this.requests.length === 1) {
                    PrimeFaces.ajax.Request.send(request);
                }
            }
        },

        poll: function() {
            if(this.isEmpty()) {
                return null;
            }

            var processed = this.requests.shift(),
            next = this.peek();

            if(next) {
                PrimeFaces.ajax.Request.send(next);
            }

            return processed;
        },

        peek: function() {
            if(this.isEmpty()) {
                return null;
            }

            return this.requests[0];
        },

        isEmpty: function() {
            return this.requests.length === 0;
        },

        addXHR: function(xhr) {
            this.xhrs.push(xhr);
        },

        removeXHR: function(xhr) {
            var index = $.inArray(xhr, this.xhrs);
            if(index > -1) {
                this.xhrs.splice(index, 1);
            }
        },

        abortAll: function() {
            for(var i = 0; i < this.xhrs.length; i++) {
                this.xhrs[i].abort();
            }

            this.xhrs = new Array();
            this.requests = new Array();
        }
    },

    Request: {

        handle: function(cfg, ext) {
            cfg.ext = ext;

            if(cfg.async) {
                PrimeFaces.ajax.Request.send(cfg);
            }
            else {
                PrimeFaces.ajax.Queue.offer(cfg);
            }
        },

        send: function(cfg) {
            PrimeFaces.debug('Initiating ajax request.');

            PrimeFaces.customFocus = false;

            var global = (cfg.global === true || cfg.global === undefined) ? true : false,
            form = null,
            sourceId = null;

            if(cfg.onstart) {
                var retVal = cfg.onstart.call(this, cfg);
                if(retVal === false) {
                    PrimeFaces.debug('Ajax request cancelled by onstart callback.');

                    //remove from queue
                    if(!cfg.async) {
                        PrimeFaces.ajax.Queue.poll();
                    }

                    return false;  //cancel request
                }
            }
            if(cfg.ext && cfg.ext.onstart) {
                cfg.ext.onstart.call(this, cfg);
            }

            if(global) {
                $(document).trigger('pfAjaxStart');
            }

            //source can be a client id or an element defined by this keyword
            if(typeof(cfg.source) === 'string') {
                sourceId = cfg.source;
            } else {
                sourceId = $(cfg.source).attr('id');
            }

            if(cfg.formId) {
                //Explicit form is defined
                form = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(cfg.formId);
            }
            else {
                //look for a parent of source
                form = $(PrimeFaces.escapeClientId(sourceId)).closest('form');

                //source has no parent form so use first form in document
                if (form.length === 0) {
                    form = $('form').eq(0);
                }
            }

            PrimeFaces.debug('Form to post ' + form.attr('id') + '.');

            var postURL = form.attr('action'),
            encodedURLfield = form.children("input[name*='javax.faces.encodedURL']"),
            postParams = [];

            //portlet support
            var porletFormsSelector = null;
            if(encodedURLfield.length > 0) {
                porletFormsSelector = 'form[action="' + postURL + '"]';
                postURL = encodedURLfield.val();
            }

            PrimeFaces.debug('URL to post ' + postURL + '.');

            // See #6857 - parameter namespace for porlet
            var parameterNamespace = PrimeFaces.ajax.Request.extractParameterNamespace(form);

            //partial ajax
            PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_REQUEST_PARAM, true, parameterNamespace);

            //source
            PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_SOURCE_PARAM, sourceId, parameterNamespace);

            //resetValues
            if (cfg.resetValues) {
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.RESET_VALUES_PARAM, true, parameterNamespace);
            }

            //ignoreAutoUpdate
            if (cfg.ignoreAutoUpdate) {
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.IGNORE_AUTO_UPDATE_PARAM, true, parameterNamespace);
            }

            //process
            var processArray = PrimeFaces.ajax.Request.resolveComponentsForAjaxCall(cfg, 'process');
            if(cfg.fragmentId) {
                processArray.push(cfg.fragmentId);
            }
            var processIds = processArray.length > 0 ? processArray.join(' ') : '@all';
            if (processIds !== '@none') {
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_PROCESS_PARAM, processIds, parameterNamespace);
            }

            //update
            var updateArray = PrimeFaces.ajax.Request.resolveComponentsForAjaxCall(cfg, 'update');
            if(cfg.fragmentId && cfg.fragmentUpdate) {
                updateArray.push(cfg.fragmentId);
            }
            if(updateArray.length > 0) {
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_UPDATE_PARAM, updateArray.join(' '), parameterNamespace);
            }

            //behavior event
            if(cfg.event) {
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.BEHAVIOR_EVENT_PARAM, cfg.event, parameterNamespace);

                var domEvent = cfg.event;

                if(cfg.event === 'valueChange')
                    domEvent = 'change';
                else if(cfg.event === 'action')
                    domEvent = 'click';

                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_EVENT_PARAM, domEvent, parameterNamespace);
            }
            else {
                PrimeFaces.ajax.Request.addParam(postParams, sourceId, sourceId, parameterNamespace);
            }

            //params
            if(cfg.params) {
                PrimeFaces.ajax.Request.addParams(postParams, cfg.params, parameterNamespace);
            }
            if(cfg.ext && cfg.ext.params) {
                PrimeFaces.ajax.Request.addParams(postParams, cfg.ext.params, parameterNamespace);
            }

            /**
             * Only add params of process components and their children
             * if partial submit is enabled and there are components to process partially
             */
            if(cfg.partialSubmit && processIds.indexOf('@all') === -1) {
                var formProcessed = false,
                partialSubmitFilter = cfg.partialSubmitFilter||':input';

                if(processIds.indexOf('@none') === -1) {
                    for (var i = 0; i < processArray.length; i++) {
                        var jqProcess = $(PrimeFaces.escapeClientId(processArray[i]));
                        var componentPostParams = null;

                        if(jqProcess.is('form')) {
                            componentPostParams = jqProcess.serializeArray();
                            formProcessed = true;
                        }
                        else if(jqProcess.is(':input')) {
                            componentPostParams = jqProcess.serializeArray();
                        }
                        else {
                            componentPostParams = jqProcess.find(partialSubmitFilter).serializeArray();
                        }

                        $.merge(postParams, componentPostParams);
                    }
                }

                //add form state if necessary
                if (!formProcessed) {
                    PrimeFaces.ajax.Request.addParamFromInput(postParams, PrimeFaces.VIEW_STATE, form, parameterNamespace);
                    PrimeFaces.ajax.Request.addParamFromInput(postParams, PrimeFaces.CLIENT_WINDOW, form, parameterNamespace);
                    PrimeFaces.ajax.Request.addParamFromInput(postParams, 'dsPostWindowId', form, parameterNamespace);
                    PrimeFaces.ajax.Request.addParamFromInput(postParams, 'dspwid', form, parameterNamespace);
                }

            }
            else {
                $.merge(postParams, form.serializeArray());
            }

            //serialize
            var postData = $.param(postParams);

            PrimeFaces.debug('Post Data:' + postData);

            var xhrOptions = {
                url : postURL,
                type : "POST",
                cache : false,
                dataType : "xml",
                data : postData,
                portletForms: porletFormsSelector,
                source: cfg.source,
                global: false,
                beforeSend: function(xhr, settings) {
                    xhr.setRequestHeader('Faces-Request', 'partial/ajax');
                    xhr.pfSettings = settings;
                    xhr.pfArgs = {}; // default should be an empty object

                    if(global) {
                        $(document).trigger('pfAjaxSend', [xhr, this]);
                    }
                },
                error: function(xhr, status, errorThrown) {
                    if(cfg.onerror) {
                        cfg.onerror.call(this, xhr, status, errorThrown);
                    }
                    if(cfg.ext && cfg.ext.onerror) {
                        cfg.ext.onerror.call(this, xhr, status, errorThrown);
                    }

                    if(global) {
                        $(document).trigger('pfAjaxError', [xhr, this, errorThrown]);
                    }

                    PrimeFaces.error('Request return with error:' + status + '.');
                },
                success: function(data, status, xhr) {
                    PrimeFaces.debug('Response received succesfully.');

                    var parsed;

                    //call user callback
                    if(cfg.onsuccess) {
                        parsed = cfg.onsuccess.call(this, data, status, xhr);
                    }

                    //extension callback that might parse response
                    if(cfg.ext && cfg.ext.onsuccess && !parsed) {
                        parsed = cfg.ext.onsuccess.call(this, data, status, xhr);
                    }

                    if(global) {
                        $(document).trigger('pfAjaxSuccess', [xhr, this]);
                    }

                    //do not execute default handler as response already has been parsed
                    if(parsed) {
                        return;
                    }
                    else {
                        PrimeFaces.ajax.Response.handle(data, status, xhr);
                    }

                    PrimeFaces.debug('DOM is updated.');
                },
                complete: function(xhr, status) {
                    if(cfg.oncomplete) {
                        cfg.oncomplete.call(this, xhr, status, xhr.pfArgs);
                    }

                    if(cfg.ext && cfg.ext.oncomplete) {
                        cfg.ext.oncomplete.call(this, xhr, status, xhr.pfArgs);
                    }

                    if(global) {
                        $(document).trigger('pfAjaxComplete', [xhr, this]);
                    }

                    PrimeFaces.debug('Response completed.');

                    PrimeFaces.ajax.Queue.removeXHR(xhr);

                    if(!cfg.async) {
                        PrimeFaces.ajax.Queue.poll();
                    }
                }
            };

            if (cfg.timeout) {
                xhrOptions['timeout'] = cfg.timeout;
            }

            PrimeFaces.ajax.Queue.addXHR($.ajax(xhrOptions));
        },

        resolveComponentsForAjaxCall: function(cfg, type) {

            var expressions = '';

            if (cfg[type]) {
                expressions += cfg[type];
            }

            if (cfg.ext && cfg.ext[type]) {
                expressions += " " + cfg.ext[type];
            }

            return PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(expressions);
        },

        addParam: function(params, name, value, parameterNamespace) {
            // add namespace if not available
            if (parameterNamespace || !name.indexOf(parameterNamespace) === 0) {
                params.push({ name:parameterNamespace + name, value:value });
            }
            else {
                params.push({ name:name, value:value });
            }

        },

        addParams: function(params, paramsToAdd, parameterNamespace) {

            for (var i = 0; i < paramsToAdd.length; i++) {
                var param = paramsToAdd[i];
                // add namespace if not available
                if (parameterNamespace && !param.name.indexOf(parameterNamespace) === 0) {
                    param.name = parameterNamespace + param.name;
                }

                params.push(param);
            }
        },

        addParamFromInput: function(params, name, form, parameterNamespace) {
            var input = null;
            if (parameterNamespace) {
                input = form.children("input[name*='" + name + "']");
            }
            else {
                input = form.children("input[name='" + name + "']");
            }

            if (input && input.length > 0) {
                var value = input.val();
                PrimeFaces.ajax.Request.addParam(params, name, value, parameterNamespace);
            }
        },

        extractParameterNamespace: function(form) {
            var input = form.children("input[name*='" + PrimeFaces.VIEW_STATE + "']");
            if (input && input.length > 0) {
                var name = input[0].name;
                if (name.length > PrimeFaces.VIEW_STATE.length) {
                    return name.substring(0, name.indexOf(PrimeFaces.VIEW_STATE));
                }
            }

            return null;
        }
    },

    Response: {

        handle: function(xml, status, xhr, updateHandler) {
            var partialResponseNode = xml.getElementsByTagName("partial-response")[0];

            for (var i = 0; i < partialResponseNode.childNodes.length; i++) {
                var currentNode = partialResponseNode.childNodes[i];

                switch (currentNode.nodeName) {
                    case "redirect":
                        PrimeFaces.ajax.ResponseProcessor.doRedirect(currentNode);
                        break;

                    case "changes":
                        var activeElementId = $(document.activeElement).attr('id');

                        for (var j = 0; j < currentNode.childNodes.length; j++) {
                            var currentChangeNode = currentNode.childNodes[j];
                            switch (currentChangeNode.nodeName) {
                                case "update":
                                    PrimeFaces.ajax.ResponseProcessor.doUpdate(currentChangeNode, xhr, updateHandler);
                                    break;
                                case "delete":
                                    PrimeFaces.ajax.ResponseProcessor.doDelete(currentChangeNode);
                                    break;
                                case "insert":
                                    PrimeFaces.ajax.ResponseProcessor.doInsert(currentChangeNode);
                                    break;
                                case "attributes":
                                    PrimeFaces.ajax.ResponseProcessor.doAttributes(currentChangeNode);
                                    break;
                                case "eval":
                                    PrimeFaces.ajax.ResponseProcessor.doEval(currentChangeNode);
                                    break;
                                case "extension":
                                    PrimeFaces.ajax.ResponseProcessor.doExtension(currentChangeNode, xhr);
                                    break;
                            }
                        }

                        PrimeFaces.ajax.Response.handleReFocus(activeElementId);
                        PrimeFaces.ajax.Response.destroyDetachedWidgets();
                        break;

                    case "eval":
                        PrimeFaces.ajax.ResponseProcessor.doEval(currentNode);
                        break;

                    case "extension":
                        PrimeFaces.ajax.ResponseProcessor.doExtension(currentNode, xhr);
                        break;

                    case "error":
                        PrimeFaces.ajax.ResponseProcessor.doError(currentNode, xhr);
                        break;
                }
            }
        },

        handleReFocus : function(activeElementId) {
            // re-focus element
            if (PrimeFaces.customFocus === false
                    && activeElementId
                    // do we really need to refocus? we just check the current activeElement here
                    && activeElementId !== $(document.activeElement).attr('id')) {

                var elementToFocus = $(PrimeFaces.escapeClientId(activeElementId));
                elementToFocus.focus();

                // double check it - required for IE
                setTimeout(function() {
                    if (!elementToFocus.is(":focus")) {
                        elementToFocus.focus();
                    }
                }, 150);
            }

            PrimeFaces.customFocus = false;
        },

        destroyDetachedWidgets : function() {
            // destroy detached widgets
            for (var i = 0; i < PrimeFaces.detachedWidgets.length; i++) {
                var widgetVar = PrimeFaces.detachedWidgets[i];

                var widget = PF(widgetVar);
                if (widget) {
                    if (widget.isDetached()) {
                        PrimeFaces.widgets[widgetVar] = null;
                        widget.destroy();

                        try {
                            delete widget;
                        } catch (e) {}
                    }
                }
            }

            PrimeFaces.detachedWidgets = [];
        }
    },

    ResponseProcessor: {

        doRedirect : function(node) {
            window.location = node.getAttribute('url');
        },

        doUpdate : function(node, xhr, updateHandler) {
            var id = node.getAttribute('id'),
            content = PrimeFaces.ajax.Utils.getContent(node);

            if (updateHandler && updateHandler.widget && updateHandler.widget.id === id) {
                updateHandler.handle.call(updateHandler.widget, content);
            } else {
                PrimeFaces.ajax.Utils.updateElement(id, content, xhr);
            }
        },

        doEval : function(node) {
            var textContent = node.textContent || node.innerText || node.text;
            $.globalEval(textContent);
        },

        doExtension : function(node, xhr) {
            if (xhr) {
                if (node.getAttribute("ln") === "primefaces" && node.getAttribute("type") === "args") {
                    var textContent = node.textContent || node.innerText || node.text;
                    // it's possible that pfArgs are already defined e.g. if portlet parameter namespacing is enabled
                    // the "parameterNamespace" will be encoded on document start
                    // the other parameters will be encoded on document end
                    // --> see PrimePartialResponseWriter
                    if (xhr.pfArgs) {
                        var json = $.parseJSON(textContent);
                        for (var name in json) {
                            xhr.pfArgs[name] = json[name];
                        }
                    }
                    else {
                        xhr.pfArgs = $.parseJSON(textContent);
                    }
                }
            }
        },

        doError : function(node, xhr) {
            // currently nothing...
        },

        doDelete : function(node) {
            var id = node.getAttribute('id');
            $(PrimeFaces.escapeClientId(id)).remove();
        },

        doInsert : function(node) {
            if (!node.childNodes) {
                return false;
            }

            for (var i = 0; i < node.childNodes.length; i++) {
                var childNode = node.childNodes[i];
                var id = childNode.getAttribute('id');
                var jq = $(PrimeFaces.escapeClientId(id));
                var content = PrimeFaces.ajax.Utils.getContent(childNode);

                if (childNode.nodeName === "after") {
                    $(content).insertAfter(jq);
                }
                else if (childNode.nodeName === "before") {
                    $(content).insertBefore(jq);
                }
            }
        },

        doAttributes : function(node) {
            if (!node.childNodes) {
                return false;
            }

            var id = node.getAttribute('id');
            var jq = $(PrimeFaces.escapeClientId(id));

            for (var i = 0; i < node.childNodes.length; i++) {
                var attrNode = node.childNodes[i];
                var attrName = attrNode.getAttribute("name");
                var attrValue = attrNode.getAttribute("value");

                if (!attrName) {
                    return;
                }

                if (!attrValue || attrValue === null) {
                    attrValue = "";
                }

                jq.attr(attrName, attrValue);
            }
        }
    },

    //Backward compatibility
    AjaxRequest: function(cfg, ext) {
        return PrimeFaces.ajax.Request.handle(cfg, ext);
    }
};

PrimeFaces.expressions = {};

PrimeFaces.expressions.SearchExpressionFacade = {

	resolveComponentsAsSelector: function(expressions) {

		var splittedExpressions = PrimeFaces.expressions.SearchExpressionFacade.splitExpressions(expressions);
		var elements = $();

		if (splittedExpressions) {
			for (var i = 0; i < splittedExpressions.length; ++i) {
				var expression =  $.trim(splittedExpressions[i]);
				if (expression.length > 0) {

					// skip unresolvable keywords
					if (expression == '@none' || expression == '@all') {
						continue;
					}

					// just a id
					if (expression.indexOf("@") == -1) {
						elements = elements.add(
								$(document.getElementById(expression)));
					}
					// @widget
					else if (expression.indexOf("@widgetVar(") == 0) {
						var widgetVar = expression.substring(11, expression.length - 1);
						var widget = PrimeFaces.widgets[widgetVar];

						if (widget) {
							elements = elements.add(
									$(document.getElementById(widget.id)));
						} else {
							PrimeFaces.error("Widget for widgetVar \"" + widgetVar + "\" not avaiable");
						}
					}
					// PFS
					else if (expression.indexOf("@(") == 0) {
						//converts pfs to jq selector e.g. @(div.mystyle :input) to div.mystyle :input
						elements = elements.add(
								$(expression.substring(2, expression.length - 1)));
					}
				}
			}
		}

		return elements;
	},

	resolveComponents: function(expressions) {
		var splittedExpressions = PrimeFaces.expressions.SearchExpressionFacade.splitExpressions(expressions),
		ids = [];

		if (splittedExpressions) {
			for (var i = 0; i < splittedExpressions.length; ++i) {
				var expression =  $.trim(splittedExpressions[i]);
				if (expression.length > 0) {

					// just a id or passtrough keywords
					if (expression.indexOf("@") == -1 || expression == '@none' || expression == '@all') {
						if (!PrimeFaces.inArray(ids, expression)) {
							ids.push(expression);
						}
					}
					// @widget
					else if (expression.indexOf("@widgetVar(") == 0) {
						var widgetVar = expression.substring(11, expression.length - 1),
						widget = PrimeFaces.widgets[widgetVar];

						if (widget) {
							if (!PrimeFaces.inArray(ids, widget.id)) {
								ids.push(widget.id);
							}
						} else {
							PrimeFaces.error("Widget for widgetVar \"" + widgetVar + "\" not avaiable");
						}
					}
					// PFS
					else if (expression.indexOf("@(") == 0) {
						//converts pfs to jq selector e.g. @(div.mystyle :input) to div.mystyle :input
						var elements = $(expression.substring(2, expression.length - 1));

						for (var j = 0; j < elements.length; j++) {
							var element = $(elements[j]),
							clientId = element.data(PrimeFaces.CLIENT_ID_DATA) || element.attr('id');

							if (!PrimeFaces.inArray(ids, clientId)) {
								ids.push(clientId);
							}
						}
					}
				}
			}
		}

		return ids;
	},

	splitExpressions: function(expression) {

		if (PrimeFaces.isIE(7)) {
			expression = expression.split('');
		}

		var expressions = [];
		var buffer = '';

		var parenthesesCounter = 0;

		for (var i = 0; i < expression.length; i++) {
			var c = expression[i];

			if (c === '(') {
				parenthesesCounter++;
			}

			if (c === ')') {
				parenthesesCounter--;
			}

			if ((c === ' ' || c === ',') && parenthesesCounter === 0) {
				// lets add token inside buffer to our tokens
				expressions.push(buffer);
				// now we need to clear buffer
				buffer = '';
			} else {
				buffer += c;
			}
		}

		// lets not forget about part after the separator
		expressions.push(buffer);

		return expressions;
	}	
};

/* Simple JavaScript Inheritance
 * By John Resig http://ejohn.org/
 * MIT Licensed.
 */
// Inspired by base2 and Prototype
(function(){
  var initializing = false, fnTest = /xyz/.test(function(){xyz;}) ? /\b_super\b/ : /.*/;
  // The base Class implementation (does nothing)
  this.Class = function(){};

  // Create a new Class that inherits from this class
  Class.extend = function(prop) {
    var _super = this.prototype;

    // Instantiate a base class (but only create the instance,
    // don't run the init constructor)
    initializing = true;
    var prototype = new this();
    initializing = false;

    // Copy the properties over onto the new prototype
    for (var name in prop) {
      // Check if we're overwriting an existing function
      prototype[name] = typeof prop[name] == "function" && 
        typeof _super[name] == "function" && fnTest.test(prop[name]) ?
        (function(name, fn){
          return function() {
            var tmp = this._super;

            // Add a new ._super() method that is the same method
            // but on the super-class
            this._super = _super[name];

            // The method only need to be bound temporarily, so we
            // remove it when we're done executing
            var ret = fn.apply(this, arguments);        
            this._super = tmp;

            return ret;
          };
        })(name, prop[name]) :
        prop[name];
    }

    // The dummy class constructor
    function Class() {
      // All construction is actually done in the init method
      if ( !initializing && this.init )
        this.init.apply(this, arguments);
    }

    // Populate our constructed prototype object
    Class.prototype = prototype;

    // Enforce the constructor to be what we expect
    Class.prototype.constructor = Class;

    // And make this class extendable
    Class.extend = arguments.callee;

    return Class;
  };
})();

PrimeFaces.widget = {};

/**
 * BaseWidget for PrimeFaces Widgets
 */
PrimeFaces.widget.BaseWidget = Class.extend({

    init: function(cfg) {
        this.cfg = cfg;
        this.id = cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jq = $(this.jqId);
        this.widgetVar = cfg.widgetVar;
        
        //remove script tag
        $(this.jqId + '_s').remove();
        
        if (this.widgetVar) {
            var $this = this;
            this.jq.on("remove", function() {
            	PrimeFaces.detachedWidgets.push($this.widgetVar);
            });
        }
    },

    //used in ajax updates, reloads the widget configuration
    refresh: function(cfg) {
        return this.init(cfg);
    },
    
    //will be called when the widget after a ajax request if the widget is detached
    destroy: function() {
    	PrimeFaces.debug("Destroyed detached widget: " + this.widgetVar);
    },

    //checks if the given widget is detached
    isDetached: function() {
    	return document.getElementById(this.id) === null;
    },

    //returns jquery object representing the main dom element related to the widget
    getJQ: function(){
        return this.jq;
    },

	/**
	 * Removes the widget's script block from the DOM.
	 *
	 * @param {string} clientId The id of the widget.
	 */
    removeScriptElement: function(clientId) {
    	$(PrimeFaces.escapeClientId(clientId) + '_s').remove();
    }
});
    
/**
 * Widgets that require to be visible to initialize properly for hidden container support
 */
PrimeFaces.widget.DeferredWidget = PrimeFaces.widget.BaseWidget.extend({

    renderDeferred: function() {     
        if(this.jq.is(':visible')) {
            this._render();
            this.postRender();
        }
        else {
            var container = this.jq.closest('.ui-hidden-container'),
            $this = this;
    
            if(container.length) {
                this.addDeferredRender(this.id, container, function() {
                    return $this.render();
                });
            }
        }
    },
    
    render: function() {
        if(this.jq.is(':visible')) {
            this._render();
            this.postRender();
            return true;
        }
        else {
            return false;
        }
    },
    
    /**
     * Must be overriden
     */
    _render: function() {
        throw 'Unsupported Operation';
    },
    
    postRender: function() {
        
    },
    
    destroy: function() {
        this._super();
        PrimeFaces.removeDeferredRenders(this.id);
    },
        
    addDeferredRender: function(widgetId, container, callback) {
        PrimeFaces.addDeferredRender(widgetId, container.attr('id'), callback);
        
        if(container.is(':hidden')) {
            var parentContainer = this.jq.closest('.ui-hidden-container');
            
            if(parentContainer.length) {
                this.addDeferredRender(widgetId, container.parent().closest('.ui-hidden-container'), callback);
            }
        }
    }
});
PrimeFaces.dialog = {};

PrimeFaces.dialog.DialogHandler = {
		
    openDialog: function(cfg) {
        var dialogId = cfg.sourceComponentId + '_dlg';
        if(document.getElementById(dialogId)) {
            return;
        }

        var dialogWidgetVar = cfg.sourceComponentId.replace(/:/g, '_') + '_dlgwidget',
        dialogDOM = $('<div id="' + dialogId + '" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-hidden-container ui-overlay-hidden"' + 
                ' data-pfdlgcid="' + cfg.pfdlgcid + '" data-widgetvar="' + dialogWidgetVar + '"></div>')
                .append('<div class="ui-dialog-titlebar ui-widget-header ui-helper-clearfix ui-corner-top"><span class="ui-dialog-title"></span></div>');
        
        var titlebar = dialogDOM.children('.ui-dialog-titlebar');
        if(cfg.options.closable !== false) {
            titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-close ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-closethick"></span></a>');
        }
        
        if(cfg.options.minimizable) {
            titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-minimize ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-minus"></span></a>');
        }
        
        if(cfg.options.maximizable) {
            titlebar.append('<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-maximize ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-extlink"></span></a>');
        }
        
        dialogDOM.append('<div class="ui-dialog-content ui-widget-content ui-df-content" style="height: auto;">' +
                '<iframe style="border:0 none" frameborder="0"/>' + 
                '</div>');
        
        dialogDOM.appendTo(document.body);
        
        var dialogFrame = dialogDOM.find('iframe'),
        symbol = cfg.url.indexOf('?') === -1 ? '?' : '&',
        frameURL = cfg.url + symbol + 'pfdlgcid=' + cfg.pfdlgcid,
        frameWidth = cfg.options.contentWidth||640;

        dialogFrame.width(frameWidth);

        dialogFrame.on('load', function() {
            var $frame = $(this),
            headerElement = $frame.contents().find('title'),
            isCustomHeader = false;
            
            if(cfg.options.headerElement) {
                var customHeaderId = PrimeFaces.escapeClientId(cfg.options.headerElement),
                customHeaderElement = dialogFrame.contents().find(customHeaderId);
                
                if(customHeaderElement.length) {
                    headerElement = customHeaderElement;
                    isCustomHeader = true;
                }
            }
            
            if(!$frame.data('initialized')) {
                PrimeFaces.cw('DynamicDialog', dialogWidgetVar, {
                    id: dialogId,
                    position: 'center',
                    sourceComponentId: cfg.sourceComponentId,
                    sourceWidget: cfg.sourceWidget,
                    onHide: function() {
                        var $dialogWidget = this,
                        dialogFrame = this.content.children('iframe');
                        
                        if(dialogFrame.get(0).contentWindow.PrimeFaces) {
                            this.destroyIntervalId = setInterval(function() {
                                if(dialogFrame.get(0).contentWindow.PrimeFaces.ajax.Queue.isEmpty()) {
                                    clearInterval($dialogWidget.destroyIntervalId);
                                    dialogFrame.attr('src','about:blank');
                                    $dialogWidget.jq.remove();
                                }
                            }, 10);
                        }
                        else {
                            dialogFrame.attr('src','about:blank');
                            $dialogWidget.jq.remove();
                        }
                        
                        PF[dialogWidgetVar] = undefined;
                    },
                    modal: cfg.options.modal,
                    resizable: cfg.options.resizable,
                    hasIframe: true,
                    draggable: cfg.options.draggable,
                    width: cfg.options.width,
                    height: cfg.options.height,
                    minimizable: cfg.options.minimizable,
                    maximizable: cfg.options.maximizable,
                    headerElement: cfg.options.headerElement
                });
            }
            
            var title = PF(dialogWidgetVar).titlebar.children('span.ui-dialog-title');
            if(headerElement.length > 0) {
                if(isCustomHeader) {
                    title.append(headerElement);
                    headerElement.show();
                }
                else {
                    title.text(headerElement.text());
                }
            }
            
            //adjust height
            var frameHeight = null;
            if(cfg.options.contentHeight)
                frameHeight = cfg.options.contentHeight;
            else
                frameHeight = $frame.get(0).contentWindow.document.body.scrollHeight + (PrimeFaces.env.browser.webkit ? 5 : 20);
            
            $frame.css('height', frameHeight);
            
            dialogFrame.data('initialized', true);
            
            PF(dialogWidgetVar).show();
        })
        .attr('src', frameURL);
    },
    
    closeDialog: function(cfg) {
        var dlg = $(document.body).children('div.ui-dialog').filter(function() {
            return $(this).data('pfdlgcid') === cfg.pfdlgcid;
        }),
        dlgWidget = PF(dlg.data('widgetvar')),
        sourceWidget = dlgWidget.cfg.sourceWidget,
        sourceComponentId = dlgWidget.cfg.sourceComponentId,
        dialogReturnBehavior = null;

        if(sourceWidget && sourceWidget.cfg.behaviors) {
            dialogReturnBehavior = sourceWidget.cfg.behaviors['dialogReturn'];
        }
        else if(sourceComponentId) {
            var dialogReturnBehaviorStr = $(document.getElementById(sourceComponentId)).data('dialogreturn');
            if(dialogReturnBehaviorStr) {
                dialogReturnBehavior = eval('(function(ext){' + dialogReturnBehaviorStr + '})');
            }

        }
                    
        if(dialogReturnBehavior) {
            var ext = {
                    params: [
                        {name: sourceComponentId + '_pfdlgcid', value: cfg.pfdlgcid}
                    ]
                };
            
            dialogReturnBehavior.call(this, ext);
        }
        
        dlgWidget.hide();
    },
            
    showMessageInDialog: function(msg) {
        if(!this.messageDialog) {
            var messageDialogDOM = $('<div id="primefacesmessagedlg" class="ui-message-dialog ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-hidden-container"/>')
                        .append('<div class="ui-dialog-titlebar ui-widget-header ui-helper-clearfix ui-corner-top"><span class="ui-dialog-title"></span>' +
                        '<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-close ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-closethick"></span></a></div>' + 
                        '<div class="ui-dialog-content ui-widget-content" style="height: auto;"></div>')
                        .appendTo(document.body);

            PrimeFaces.cw('Dialog', 'primefacesmessagedialog', {
                id: 'primefacesmessagedlg', 
                modal:true,
                draggable: false,
                resizable: false,
                showEffect: 'fade',
                hideEffect: 'fade'
            });
            this.messageDialog = PF('primefacesmessagedialog');
            this.messageDialog.titleContainer = this.messageDialog.titlebar.children('span.ui-dialog-title');
        }

        this.messageDialog.titleContainer.text(msg.summary);
        this.messageDialog.content.html('').append('<span class="ui-dialog-message ui-messages-' + msg.severity.split(' ')[0].toLowerCase() + '-icon" />').append(msg.detail);
        this.messageDialog.show();
    },
            
    confirm: function(msg) {
        if(PrimeFaces.confirmDialog) {
            PrimeFaces.confirmSource = (typeof(msg.source) === 'string') ? $(PrimeFaces.escapeClientId(msg.source)) : $(msg.source);
            PrimeFaces.confirmDialog.showMessage(msg);
        }
        else {
            PrimeFaces.warn('No global confirmation dialog available.');
        }
    }
};
/**
 * PrimeFaces Accordion Panel Widget
 */
PrimeFaces.widget.AccordionPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.stateHolder = $(this.jqId + '_active');
        this.headers = this.jq.children('.ui-accordion-header');
        this.panels = this.jq.children('.ui-accordion-content');
        this.cfg.rtl = this.jq.hasClass('ui-accordion-rtl');
        this.cfg.expandedIcon = 'ui-icon-triangle-1-s';
        this.cfg.collapsedIcon = this.cfg.rtl ? 'ui-icon-triangle-1-w' : 'ui-icon-triangle-1-e';
        this.focusedHeader = null;
        
        this.initActive();
        this.bindEvents();

        if(this.cfg.dynamic && this.cfg.cache) {
            this.markLoadedPanels();
        }
    },
            
    initActive: function() {
        var firstActiveIndex = 0;
        
        if(this.cfg.multiple) {
            var indexes = this.stateHolder.val().split(',');
            for(var i = 0; i < indexes.length; i++) {
                indexes[i] = parseInt(indexes[i]);
            }
            
            this.cfg.active = indexes;
        }
        else {
            this.cfg.active = parseInt(this.stateHolder.val());
        }
        
        if(this.cfg.multiple) {
            firstActiveIndex = this.cfg.active[0];
        }
        else {
            firstActiveIndex = this.cfg.active;
        }
        
        for(var i = 0; i < this.headers.length; i++) {
            if(firstActiveIndex === i && !this.headers.eq(i).hasClass('ui-state-disabled')) {
                this.headers.eq(i).attr('tabindex', '0');
            }
            else {
                this.headers.eq(i).attr('tabindex', '-1');
            }
        }

        if(this.headers.filter('[tabindex="-1"]').length === this.headers.length) {
            this.headers.filter('h3:not(.ui-state-disabled):first').attr('tabindex', '0');
        }
    },
        
    bindEvents: function() {
        var $this = this;
    
        this.headers.mouseover(function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')&&!element.hasClass('ui-state-disabled')) {
                element.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')&&!element.hasClass('ui-state-disabled')) {
                element.removeClass('ui-state-hover');
            }
        }).click(function(e) {            
            var element = $(this);
            if(!element.hasClass('ui-state-disabled')) {
                var tabIndex = element.index() / 2;

                if(element.hasClass('ui-state-active')) {
                    $this.unselect(tabIndex);
                }
                else {
                    $this.select(tabIndex);
                    $(this).trigger('focus.accordion');
                }
            }

            e.preventDefault();
        });
        
        this.bindKeyEvents();
    },
    
    bindKeyEvents: function() {
        var $this = this;
        
        this.headers.on('focus.accordion', function(){
            $this.focusedHeader = $(this);
            if(!$this.focusedHeader.hasClass('ui-state-disabled')) {
                $this.headers.filter('[tabindex="0"]').attr('tabindex', '-1').removeClass('ui-tabs-outline');
                $this.focusedHeader.attr('tabindex', '0').addClass('ui-tabs-outline');
            }
        })
        .on('blur.accordion', function(){
            if($this.focusedHeader) {
                $this.focusedHeader.removeClass('ui-tabs-outline');                
            }
        })
        .on('keydown.accordion', function(e) {
            var keyCode = $.ui.keyCode;
 
            switch(e.which) {
                case keyCode.LEFT:
                case keyCode.UP: 
                    if($this.focusedHeader) {
                        if(($this.focusedHeader.index()/2) === 0) {
                            $this.focusedHeader = $this.headers.filter('h3:not(.ui-state-disabled):last');
                        }
                        else {
                            $this.focusedHeader = $this.focusedHeader.prevAll('h3:not(.ui-state-disabled):first');
                            if(!$this.focusedHeader.length) {
                                $this.focusedHeader = $this.headers.filter('h3:not(.ui-state-disabled):last');
                            }
                        }
                        $this.focusedHeader.trigger('focus.accordion');
                    }
                    e.preventDefault();
                break;

                case keyCode.RIGHT:
                case keyCode.DOWN:
                    if($this.focusedHeader) {
                        if(($this.focusedHeader.index()/2) === ($this.headers.length - 1)) {
                            $this.focusedHeader = $this.headers.filter('h3:not(.ui-state-disabled):first');
                        }
                        else {
                            $this.focusedHeader = $this.focusedHeader.nextAll('h3:not(.ui-state-disabled):first');
                            if(!$this.focusedHeader.length) {
                                $this.focusedHeader = $this.headers.filter('h3:not(.ui-state-disabled):first');
                            }
                        } 
                        $this.focusedHeader.trigger('focus.accordion');
                    }
                    e.preventDefault();
                break;
                
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                case keyCode.SPACE:
                    if($this.focusedHeader) {
                        $this.focusedHeader.trigger('click');
                    }
                    e.preventDefault();
                break;
            }       
        });
    },
            
    markLoadedPanels: function() {
        if(this.cfg.multiple) {
            for(var i = 0; i < this.cfg.active.length; i++) {
                if(this.cfg.active[i] >= 0)
                    this.markAsLoaded(this.panels.eq(this.cfg.active[i]));
            }
        } else {
            if(this.cfg.active >= 0)
                this.markAsLoaded(this.panels.eq(this.cfg.active));
        }
    },
    
    /**
     *  Activates a tab with given index
     */
    select: function(index) {
        var panel = this.panels.eq(index);

        //Call user onTabChange callback
        if(this.cfg.onTabChange) {
            var result = this.cfg.onTabChange.call(this, panel);
            if(result === false)
                return false;
        }

        var shouldLoad = this.cfg.dynamic && !this.isLoaded(panel);

        //update state
        if(this.cfg.multiple)
            this.addToSelection(index);
        else
            this.cfg.active = index;

        this.saveState();

        if(shouldLoad) {
            this.loadDynamicTab(panel);
        }
        else {
            this.show(panel);
            
            if(this.hasBehavior('tabChange')) {
                this.fireTabChangeEvent(panel);
            }
        }

        return true;
    },
    
    /**
     *  Deactivates a tab with given index
     */
    unselect: function(index) {
        var panel = this.panels.eq(index),
        header = panel.prev();

        header.attr('aria-selected', false);
        header.attr('aria-expanded', false).children('.ui-icon').removeClass(this.cfg.expandedIcon).addClass(this.cfg.collapsedIcon);
        header.removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all');
        panel.attr('aria-hidden', true).slideUp();

        this.removeFromSelection(index);
        this.saveState();
        
        if(this.hasBehavior('tabClose')) {
            this.fireTabCloseEvent(panel);
        }
    },
    
    show: function(panel) {
        var _self = this;

        //deactivate current
        if(!this.cfg.multiple) {
            var oldHeader = this.headers.filter('.ui-state-active');
            oldHeader.children('.ui-icon').removeClass(this.cfg.expandedIcon).addClass(this.cfg.collapsedIcon);
            oldHeader.attr('aria-selected', false);
            oldHeader.attr('aria-expanded', false).removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all').next().attr('aria-hidden', true).slideUp();
        }

        //activate selected
        var newHeader = panel.prev();
        newHeader.attr('aria-selected', true);
        newHeader.attr('aria-expanded', true).addClass('ui-state-active ui-corner-top').removeClass('ui-state-hover ui-corner-all')
                .children('.ui-icon').removeClass(this.cfg.collapsedIcon).addClass(this.cfg.expandedIcon);

        panel.attr('aria-hidden', false).slideDown('normal', function() {
            _self.postTabShow(panel);
        });
    },
    
    loadDynamicTab: function(panel) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true},
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: parseInt(panel.index() / 2)}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            panel.html(content);

                            if(this.cfg.cache) {
                                this.markAsLoaded(panel);
                            }   
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.show(panel);
            }
        };

        if(this.hasBehavior('tabChange')) {
            var tabChangeBehavior = this.cfg.behaviors['tabChange'];

            tabChangeBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    },
    
    fireTabChangeEvent : function(panel) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: [
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: parseInt(panel.index() / 2)}
            ]
        };
        
        tabChangeBehavior.call(this, ext);
    },

    fireTabCloseEvent : function(panel) {
        var tabCloseBehavior = this.cfg.behaviors['tabClose'],
        ext = {
            params: [
                {name: this.id + '_tabId', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: parseInt(panel.index() / 2)}
            ]
        };
        
        tabCloseBehavior.call(this, ext);
    },
    
    markAsLoaded: function(panel) {
        panel.data('loaded', true);
    },

    isLoaded: function(panel) {
        return panel.data('loaded') == true;
    },

    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    },

    addToSelection: function(nodeId) {
        this.cfg.active.push(nodeId);
    },

    removeFromSelection: function(nodeId) {
        this.cfg.active = $.grep(this.cfg.active, function(r) {
            return r != nodeId;
        });
    },
    
    saveState: function() {
        if(this.cfg.multiple)
            this.stateHolder.val(this.cfg.active.join(','));
        else
            this.stateHolder.val(this.cfg.active);
    },

    postTabShow: function(newPanel) {            
        //Call user onTabShow callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel);
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    }
    
});
/**
 * PrimeFaces AjaxStatus Widget
 */
PrimeFaces.widget.AjaxStatus = PrimeFaces.widget.BaseWidget.extend({
             
    init: function(cfg) {
        this._super(cfg);

        this.bind();
    },
            
    bind: function() {
        var doc = $(document),
        $this = this;
        
        doc.on('pfAjaxStart', function() {
            $this.trigger('start', arguments);
        })
        .on('pfAjaxError', function() {
            $this.trigger('error', arguments);
        })
        .on('pfAjaxSuccess', function() {
            $this.trigger('success', arguments);
        })
        .on('pfAjaxComplete', function() {
            $this.trigger('complete', arguments);
        });
        
        this.bindToStandard();
    },
            
    trigger: function(event, args) {
        var callback = this.cfg[event];
        if(callback) {
            callback.apply(document, args);
        }
        
        this.jq.children().hide().filter(this.jqId + '_' + event).show();
    },
         
    bindToStandard: function() {
        if(window.jsf && window.jsf.ajax) {
        	var doc = $(document);

        	jsf.ajax.addOnEvent(function(data) {
                if(data.status === 'begin') {
                    doc.trigger('pfAjaxStart', arguments);
                }
                else if(data.status === 'complete') {
                    doc.trigger('pfAjaxSuccess', arguments);
                }
                else if(data.status === 'success') {
                    doc.trigger('pfAjaxComplete', arguments);
                }
            });

            jsf.ajax.addOnError(function(data) {
                doc.trigger('pfAjaxError', arguments);
            });
        }
    }
    
});
/**
 * PrimeFaces AutoComplete Widget
 */
PrimeFaces.widget.AutoComplete = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.panelId = this.jqId + '_panel';
        this.input = $(this.jqId + '_input');
        this.hinput = $(this.jqId + '_hinput');
        this.panel = this.jq.children(this.panelId);
        this.dropdown = this.jq.children('.ui-button');
        this.active = true;
        this.cfg.pojo = this.hinput.length == 1;
        this.cfg.minLength = this.cfg.minLength != undefined ? this.cfg.minLength : 1;
        this.cfg.cache = this.cfg.cache||false;
        this.cfg.resultsMessage = this.cfg.resultsMessage||' results are available, use up and down arrow keys to navigate';
        this.cfg.ariaEmptyMessage = this.cfg.emptyMessage||'No search results are available.';
        this.cfg.dropdownMode = this.cfg.dropdownMode||'blank';
        this.cfg.autoHighlight = (this.cfg.autoHighlight === undefined) ? true : this.cfg.autoHighlight;
        this.cfg.myPos = this.cfg.myPos||'left top';
        this.cfg.atPos = this.cfg.atPos||'left bottom';
        this.cfg.active = (this.cfg.active === false) ? false : true;
        this.suppressInput = true;
        this.touchToDropdownButton = false;
        
        if(this.cfg.cache) {
            this.initCache();
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hinput.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        if(this.cfg.multiple) {
            this.setupMultipleMode();

            this.multiItemContainer.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
            
            if(this.cfg.selectLimit >= 0 && this.multiItemContainer.children('li.ui-autocomplete-token').length === this.cfg.selectLimit) {
                this.input.hide();
            }
        }
        else {
            //visuals
            PrimeFaces.skinInput(this.input);

            this.input.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
            this.dropdown.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
        }

        //core events
        this.bindStaticEvents();

        //client Behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
        }

        //force selection
        if(this.cfg.forceSelection) {
            this.setupForceSelection();
        }

        //Panel management
        this.appendPanel();

        //itemtip
        if(this.cfg.itemtip) {
            this.itemtip = $('<div id="' + this.id + '_itemtip" class="ui-autocomplete-itemtip ui-state-highlight ui-widget ui-corner-all ui-shadow"></div>').appendTo(document.body);
            this.cfg.itemtipMyPosition = this.cfg.itemtipMyPosition||'left top';
            this.cfg.itemtipAtPosition = this.cfg.itemtipAtPosition||'right bottom';
            this.cfg.checkForScrollbar = (this.cfg.itemtipAtPosition.indexOf('right') !== -1);
        }
        
        //aria
        this.input.attr('aria-autocomplete', 'listbox');
        this.jq.attr('role', 'application');
        this.jq.append('<span role="status" aria-live="polite" class="ui-autocomplete-status ui-helper-hidden-accessible"></span>');
        this.status = this.jq.children('.ui-autocomplete-status');
    },

    appendPanel: function() {
        var container = this.cfg.appendTo ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo): $(document.body);

        if(!container.is(this.jq)) {
            container.children(this.panelId).remove();
            this.panel.appendTo(container);
        }
    },

    initCache: function() {
        this.cache = {};
        var $this=this;

        this.cacheTimeout = setInterval(function(){
            $this.clearCache();
        }, this.cfg.cacheTimeout);
    },

    clearCache: function() {
        this.cache = {};
    },

    /**
     * Binds events for multiple selection mode
     */
    setupMultipleMode: function() {
        var $this = this;
        this.multiItemContainer = this.jq.children('ul');
        this.inputContainer = this.multiItemContainer.children('.ui-autocomplete-input-token');

        this.multiItemContainer.hover(function() {
                $(this).addClass('ui-state-hover');
            },
            function() {
                $(this).removeClass('ui-state-hover');
            }
        ).click(function() {
            $this.input.focus();
        });

        //delegate events to container
        this.input.focus(function() {
            $this.multiItemContainer.addClass('ui-state-focus');
        }).blur(function(e) {
            $this.multiItemContainer.removeClass('ui-state-focus');
        });

        var closeSelector = '> li.ui-autocomplete-token > .ui-autocomplete-token-icon';
        this.multiItemContainer.off('click', closeSelector).on('click', closeSelector, null, function(event) {
            if($this.multiItemContainer.children('li.ui-autocomplete-token').length === $this.cfg.selectLimit) {
                if(PrimeFaces.isIE(8)) {
                    $this.input.val('');
                }
                $this.input.css('display', 'inline');
            }
            $this.removeItem(event, $(this).parent());
        });
    },

    bindStaticEvents: function() {
        var $this = this;

        this.bindKeyEvents();

        this.dropdown.mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).mousedown(function() {
            if($this.active) {
                $(this).addClass('ui-state-active');
            }
        }).mouseup(function() {
            if($this.active) {
                $(this).removeClass('ui-state-active');

                $this.searchWithDropdown();
                $this.input.focus();
            }
        }).focus(function() {
            $(this).addClass('ui-state-focus');
        }).blur(function() {
            $(this).removeClass('ui-state-focus');
        }).keydown(function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;
            
            if(key === keyCode.SPACE || key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                $(this).addClass('ui-state-active');
            }
        }).keyup(function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;
            
            if(key === keyCode.SPACE || key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                $(this).removeClass('ui-state-active');
                $this.searchWithDropdown();
                $this.input.focus();
                e.preventDefault();
                e.stopPropagation();
            }
        });

        if(PrimeFaces.env.browser.mobile) {
            this.dropdown.bind('touchstart', function() {
                $this.touchToDropdownButton = true;
            });
        }
        
        //hide overlay when outside is clicked
        this.hideNS = 'mousedown.' + this.id;
        $(document.body).off(this.hideNS).on(this.hideNS, function (e) {
            if($this.panel.is(":hidden")) {
                return;
            }
            
            var offset = $this.panel.offset();
            if(e.target === $this.input.get(0)) {
                return;
            }
            
            if (e.pageX < offset.left ||
                e.pageX > offset.left + $this.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.panel.height()) {
                $this.hide();
            }
        });

        this.resizeNS = 'resize.' + this.id;
        $(window).off(this.resizeNS).on(this.resizeNS, function(e) {
            if($this.panel.is(':visible')) {
                $this.alignPanel();
            }
        });
    },

    bindKeyEvents: function() {
        var $this = this;

        if(this.cfg.queryEvent !== 'enter') {
            this.input.on('input propertychange', function(e) {
                if($this.suppressInput) {
                    e.preventDefault();
                    return;
                }
                
                // for touch event on mobile
                if(PrimeFaces.env.browser.mobile) {
                    $this.touchToDropdownButton = false;
                    if($this.itemClick) {
                        $this.itemClick = false;
                        return;
                    }
                }
                
                // for click event on IE8
                if(PrimeFaces.isIE(8) && ($this.itemClick || e.originalEvent.propertyName !== 'value')) {
                    $this.itemClick = false;
                    return;
                }
                
                var value = $this.input.val();

                if($this.cfg.pojo && !$this.cfg.multiple) {
                    $this.hinput.val(value);
                }

                if(!value.length) {
                    $this.hide();
                }

                if(value.length >= $this.cfg.minLength) {
                    if($this.timeout) {
                        $this.deleteTimeout();
                    }

                    var delay = $this.cfg.delay;
                    $this.timeout = setTimeout(function() {
                        $this.timeout = null;
                        $this.search(value);
                    }, delay);
                }
            });
        }
        
        this.input.on('keyup.autoComplete', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;
            
            if($this.cfg.queryEvent === 'enter' && (key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER)) {
                if($this.itemSelectedWithEnter)
                    $this.itemSelectedWithEnter = false;
                else
                    $this.search($this.input.val());
            }
            
            if($this.panel.is(':visible')) {
                if(key === keyCode.ESCAPE) {
                    $this.hide();
                }
                else if(key === keyCode.UP || key === keyCode.DOWN) {
                    var highlightedItem = $this.items.filter('.ui-state-highlight');
                    if(highlightedItem.length) {
                        $this.displayAriaStatus(highlightedItem.data('item-label'));
                    }
                }
            }
        }).on('keydown.autoComplete', function(e) {
            var keyCode = $.ui.keyCode;

            $this.suppressInput = false;
            if($this.panel.is(':visible')) {
                var highlightedItem = $this.items.filter('.ui-state-highlight');

                switch(e.which) {
                    case keyCode.UP:
                        var prev = highlightedItem.length == 0 ? $this.items.eq(0) : highlightedItem.prevAll('.ui-autocomplete-item:first');

                        if(prev.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            prev.addClass('ui-state-highlight');

                            if($this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView($this.panel, prev);
                            }

                            if($this.cfg.itemtip) {
                                $this.showItemtip(prev);
                            }
                        }

                        e.preventDefault();
                        break;

                    case keyCode.DOWN:
                        var next = highlightedItem.length == 0 ? $this.items.eq(0) : highlightedItem.nextAll('.ui-autocomplete-item:first');

                        if(next.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            next.addClass('ui-state-highlight');

                            if($this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView($this.panel, next);
                            }

                            if($this.cfg.itemtip) {
                                $this.showItemtip(next);
                            }
                        }

                        e.preventDefault();
                        break;

                    case keyCode.ENTER:
                    case keyCode.NUMPAD_ENTER:
                        if ($this.timeout) {
                            $this.deleteTimeout();
                        }
                    
                        highlightedItem.click();

                        e.preventDefault();
                        e.stopPropagation();
                        $this.itemSelectedWithEnter = true;
                        break;

                    case 18: //keyCode.ALT:
                    case 224:
                        break;

                    case keyCode.TAB:
                        if(highlightedItem.length) {
                            highlightedItem.trigger('click');
                        }
                        $this.hide();
                        break;
                }
            }
            else {
                switch(e.which) {
                    case keyCode.TAB:
                        if ($this.timeout) {
                            $this.deleteTimeout();
                        }
                    break;
                    
                    case keyCode.ENTER:
                    case keyCode.NUMPAD_ENTER:                        
                        if($this.cfg.queryEvent === 'enter' || ($this.timeout > 0) || $this.querying) {
                            e.preventDefault();
                        }
                    break;
                };
            }
            
        });
    },

    bindDynamicEvents: function() {
        var $this = this;

        //visuals and click handler for items
        this.items.bind('mouseover', function() {
            var item = $(this);

            if(!item.hasClass('ui-state-highlight')) {
                $this.items.filter('.ui-state-highlight').removeClass('ui-state-highlight');
                item.addClass('ui-state-highlight');

                if($this.cfg.itemtip) {
                    $this.showItemtip(item);
                }
            }
        })
        .bind('click', function(event) {
            var item = $(this),
            itemValue = item.attr('data-item-value');
            
            if(PrimeFaces.isIE(8)) {
                $this.itemClick = true;
            }

            if($this.cfg.multiple) {
                var itemDisplayMarkup = '<li data-token-value="' + item.attr('data-item-value') + '"class="ui-autocomplete-token ui-state-active ui-corner-all ui-helper-hidden">';
                itemDisplayMarkup += '<span class="ui-autocomplete-token-icon ui-icon ui-icon-close" />';
                itemDisplayMarkup += '<span class="ui-autocomplete-token-label">' + item.attr('data-item-label') + '</span></li>';

                $this.inputContainer.before(itemDisplayMarkup);
                $this.multiItemContainer.children('.ui-helper-hidden').fadeIn();
                $this.input.val('').focus();

                $this.hinput.append('<option value="' + itemValue + '" selected="selected"></option>');
                if($this.multiItemContainer.children('li.ui-autocomplete-token').length >= $this.cfg.selectLimit) {
                    $this.input.css('display', 'none').blur();
                }
            }
            else {
                $this.input.val(item.attr('data-item-label')).focus();

                this.currentText = $this.input.val();
                this.previousText = $this.input.val();

                if($this.cfg.pojo) {
                    $this.hinput.val(itemValue);
                }
                
                if(PrimeFaces.env.isLtIE(10)) {
                    var length = $this.input.val().length;
                    $this.input.setSelection(length,length);
                }
            }

            $this.invokeItemSelectBehavior(event, itemValue);

            $this.hide();
        });
        
        if(PrimeFaces.env.browser.mobile) {
            this.items.bind('touchstart', function() {
                if(!$this.touchToDropdownButton) {
                    $this.itemClick = true;
                }
            });
        }
    },

    showItemtip: function(item) {
        var content = item.is('li') ? item.next('.ui-autocomplete-itemtip-content') : item.children('td:last');

        this.itemtip.html(content.html())
                    .css({
                        'left':'',
                        'top':'',
                        'z-index': ++PrimeFaces.zindex,
                        'width': content.outerWidth()
                    })
                    .position({
                        my: this.cfg.itemtipMyPosition
                        ,at: this.cfg.itemtipAtPosition
                        ,of: item
                    });

        //scrollbar offset
        if(this.cfg.checkForScrollbar) {
            if(this.panel.innerHeight() < this.panel.children('.ui-autocomplete-items').outerHeight(true)) {
                var panelOffset = this.panel.offset();
                this.itemtip.css('left', panelOffset.left + this.panel.outerWidth());
            }
        }

        this.itemtip.show();
    },

    showSuggestions: function(query) {
        this.items = this.panel.find('.ui-autocomplete-item');
        this.items.attr('role', 'option');
        
        if(this.cfg.grouping) {
            this.groupItems();
        }
        
        this.bindDynamicEvents();

        var $this=this,
        hidden = this.panel.is(':hidden');

        if(hidden) {
            this.show();
        }
        else {
            this.alignPanel();
        }

        if(this.items.length > 0) {
            var firstItem = this.items.eq(0);

            //highlight first item
            if(this.cfg.autoHighlight && firstItem.length) {
                firstItem.addClass('ui-state-highlight');
            }

            //highlight query string
            if(this.panel.children().is('ul') && query.length > 0) {
                this.items.each(function() {
                    var item = $(this),
                    text = item.html(),
                    re = new RegExp(PrimeFaces.escapeRegExp(query), 'gi'),
                    highlighedText = text.replace(re, '<span class="ui-autocomplete-query">$&</span>');

                    item.html(highlighedText);
                });
            }

            if(this.cfg.forceSelection) {
                this.currentItems = [];
                this.items.each(function(i, item) {
                    $this.currentItems.push($(item).attr('data-item-label'));
                });
            }

            //show itemtip if defined
            if(this.cfg.itemtip && firstItem.length === 1) {
                this.showItemtip(firstItem);
            }
            
            this.displayAriaStatus(this.items.length + this.cfg.resultsMessage);
        }
        else {
            if(this.cfg.emptyMessage) {
                var emptyText = '<div class="ui-autocomplete-emptyMessage ui-widget">'+this.cfg.emptyMessage+'</div>';
                this.panel.html(emptyText);
            }
            else {
                this.panel.hide();
            }
            
            this.displayAriaStatus(this.cfg.ariaEmptyMessage);
        }
    },
    
    searchWithDropdown: function() {
        if(this.cfg.dropdownMode === 'current')
            this.search(this.input.val());
        else
            this.search('');
    },

    search: function(query) {
        //allow empty string but not undefined or null
        if(!this.cfg.active || query === undefined || query === null) {
            return;
        }

        if(this.cfg.cache && this.cache[query]) {
            this.panel.html(this.cache[query]);
            this.showSuggestions(query);
            return;
        }

        if(!this.active) {
            return;
        }
        
        this.querying = true;

        var $this = this;

        if(this.cfg.itemtip) {
            this.itemtip.hide();
        }

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId,
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        this.panel.html(content);

                        if(this.cfg.cache) {
                            this.cache[query] = content;
                        }

                        this.showSuggestions(query);
                    }
                });

                return true;
            },
            oncomplete: function() {
                $this.querying = false;
            }
        };

        options.params = [
          {name: this.id + '_query', value: query}
        ];

        if(this.hasBehavior('query')) {
            var queryBehavior = this.cfg.behaviors['query'];
            queryBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    },

    show: function() {
        this.alignPanel();

        if(this.cfg.effect)
            this.panel.show(this.cfg.effect, {}, this.cfg.effectDuration);
        else
            this.panel.show();
    },

    hide: function() {
        this.panel.hide();
        this.panel.css('height', 'auto');

        if(this.cfg.itemtip) {
            this.itemtip.hide();
        }
    },

    invokeItemSelectBehavior: function(event, itemValue) {
        if(this.cfg.behaviors) {
            var itemSelectBehavior = this.cfg.behaviors['itemSelect'];

            if(itemSelectBehavior) {
                var ext = {
                    params : [
                        {name: this.id + '_itemSelect', value: itemValue}
                    ]
                };

                itemSelectBehavior.call(this, ext);
            }
        }
    },

    invokeItemUnselectBehavior: function(event, itemValue) {
        if(this.cfg.behaviors) {
            var itemUnselectBehavior = this.cfg.behaviors['itemUnselect'];

            if(itemUnselectBehavior) {
                var ext = {
                    params : [
                        {name: this.id + '_itemUnselect', value: itemValue}
                    ]
                };

                itemUnselectBehavior.call(this, ext);
            }
        }
    },

    removeItem: function(event, item) {
        var itemValue = item.attr('data-token-value'),
        itemIndex = this.multiItemContainer.children('li.ui-autocomplete-token').index(item),
        $this = this;

        //remove from options
        this.hinput.children('option').eq(itemIndex).remove();

        //remove from items
        item.fadeOut('fast', function() {
            var token = $(this);

            token.remove();

            $this.invokeItemUnselectBehavior(event, itemValue);
        });
    },

    setupForceSelection: function() {
        this.currentItems = [this.input.val()];
        var $this = this;

        this.input.blur(function() {
            var value = $(this).val(),
            valid = false;
            
            if(PrimeFaces.isIE(8)) {
                $this.itemClick = true;
            }
            
            for(var i = 0; i < $this.currentItems.length; i++) {
                if($this.currentItems[i] === value) {
                    valid = true;
                    break;
                }
            }

            if(!valid) {
                if($this.cfg.multiple) {
                    $this.input.val('');
                }
                else {
                    $this.input.val('');
                    $this.hinput.val('');
                }
            }
        });
    },

    disable: function() {
        this.input.addClass('ui-state-disabled').prop('disabled', true);
        
        if(this.dropdown.length) {
            this.dropdown.addClass('ui-state-disabled').prop('disabled', true);
        }
    },

    enable: function() {
        this.input.removeClass('ui-state-disabled').prop('disabled', false);
        
        if(this.dropdown.length) {
            this.dropdown.removeClass('ui-state-disabled').prop('disabled', false);
        }
    },
    
    close: function() {
        this.hide();
    },

    deactivate: function() {
        this.active = false;
    },

    activate: function() {
        this.active = true;
    },

    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    },

    alignPanel: function() {
        var panelWidth = null;

        if(this.cfg.multiple) {
            panelWidth = this.multiItemContainer.innerWidth() - (this.input.position().left - this.multiItemContainer.position().left);
        }
        else {
            if(this.panel.is(':visible')) {
                panelWidth = this.panel.children('.ui-autocomplete-items').outerWidth();
            }
            else {
                this.panel.css({'visibility':'hidden','display':'block'});
                panelWidth = this.panel.children('.ui-autocomplete-items').outerWidth();
                this.panel.css({'visibility':'visible','display':'none'});
            }

            var inputWidth = this.input.outerWidth();
            if(panelWidth < inputWidth) {
                panelWidth = inputWidth;
            }
        }

        if(this.cfg.scrollHeight) {
            var heightConstraint = this.panel.is(':hidden') ? this.panel.height() : this.panel.children().height();
            if(heightConstraint > this.cfg.scrollHeight)
                this.panel.height(this.cfg.scrollHeight);
            else
                this.panel.css('height', 'auto');
        }

        this.panel.css({'left':'',
                        'top':'',
                        'width': panelWidth,
                        'z-index': ++PrimeFaces.zindex
                });

        if(this.panel.parent().is(this.jq)) {
            this.panel.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.panel.position({
                    my: this.cfg.myPos
                    ,at: this.cfg.atPos
                    ,of: this.cfg.multiple ? this.jq : this.input
                    ,collision: 'flipfit'
                });
        }
    },
    
    displayAriaStatus: function(text) {
        this.status.html('<div>' + text + '</div>');
    },
    
    groupItems: function() {
        var $this = this;
        
        if(this.items.length) {
            this.itemContainer = this.panel.children('.ui-autocomplete-items');
            this.currentGroup = this.items.eq(0).data('item-group');
            var currentGroupTooltip = this.items.eq(0).data('item-group-tooltip');
            
            this.items.eq(0).before(this.getGroupItem($this.currentGroup, $this.itemContainer, currentGroupTooltip));
            
            this.items.each(function(i) {
                var item = $this.items.eq(i),
                itemGroup = item.data('item-group'),
                itemGroupTooltip = item.data('item-group-tooltip');
                
                if($this.currentGroup !== itemGroup) {
                    $this.currentGroup = itemGroup;
                    item.before($this.getGroupItem(itemGroup, $this.itemContainer, itemGroupTooltip));
                }
            });
        }
    },
    
    getGroupItem: function(group, container, tooltip) {
        var element = null;
        
        if(container.is('.ui-autocomplete-table')) {
            if(!this.colspan) {
                this.colspan = this.items.eq(0).children('td').length;
            }
            
            element = $('<tr class="ui-autocomplete-group ui-widget-header"><td colspan="' + this.colspan + '">' + group + '</td></tr>');
        }
        else {
            element = $('<li class="ui-autocomplete-group ui-autocomplete-list-item ui-widget-header">' + group + '</li>');
        }
        
        if(element) {
            element.attr('title', tooltip);
        }
        
        return element;
    },
    
    deleteTimeout: function() {
        clearTimeout(this.timeout);
        this.timeout = null;
    }

});
/**
 * PrimeFaces BlockUI Widget
 */
PrimeFaces.widget.BlockUI = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.block = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.block);
        this.content = $(this.jqId);
        this.cfg.animate = (this.cfg.animate === false)? false : true;
        
        this.render();

        if(this.cfg.triggers) {
            this.bindTriggers();
        }
        
        if(this.cfg.blocked) {
            this.show();
        }

        this.removeScriptElement(this.id);
    },
            
    refresh: function(cfg) {
        $(document).off('pfAjaxSend.' + this.id + ' pfAjaxComplete.' + this.id);
        
        this._super(cfg);
    },
    
    bindTriggers: function() {
        var $this = this,
        triggers = PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(this.cfg.triggers);
        
        //listen global ajax send and complete callbacks
        $(document).on('pfAjaxSend.' + this.id, function(e, xhr, settings) {
            var sourceId = $.type(settings.source) === 'string' ? settings.source : settings.source.name;
            
            if($.inArray(sourceId, triggers) !== -1) {
                $this.show();
            }
        });

        $(document).on('pfAjaxComplete.' + this.id, function(e, xhr, settings) {
            var sourceId = $.type(settings.source) === 'string' ? settings.source : settings.source.name;
            
            if($.inArray(sourceId, triggers) !== -1) {
                $this.hide();
            }
        });
    },
    
    show: function() {
        this.blocker.css('z-index', ++PrimeFaces.zindex);
        
        //center position of content
        this.content.css({
            'left': (this.blocker.width() - this.content.outerWidth()) / 2,
            'top': (this.blocker.height() - this.content.outerHeight()) / 2,
            'z-index': ++PrimeFaces.zindex
        });

        if(this.cfg.animate)
            this.blocker.fadeIn();    
        else
            this.blocker.show();

        if(this.hasContent()) {
            this.content.fadeIn();
        }
    },
    
    hide: function() {
        if(this.cfg.animate)
            this.blocker.fadeOut();
        else
            this.blocker.hide();

        if(this.hasContent()) {
            this.content.fadeOut();
        }
    },
    
    render: function() {   
        this.blocker = $('<div id="' + this.id + '_blocker" class="ui-blockui ui-widget-overlay ui-helper-hidden"></div>');
        
        if(this.cfg.styleClass) {
            this.blocker.addClass(this.cfg.styleClass);
        }

        if(this.block.hasClass('ui-corner-all')) {
            this.blocker.addClass('ui-corner-all');
        }

        this.block.css('position', 'relative').append(this.blocker).append(this.content);
    },
    
    hasContent: function() {
        return this.content.contents().length > 0;
    }
    
});
/**
 * PrimeFaces Calendar Widget
 */
PrimeFaces.widget.Calendar = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.jqEl = this.cfg.popup ? this.input : $(this.jqId + '_inline');
        var _self = this;

        //i18n and l7n
        this.configureLocale();

        //events
        this.bindDateSelectListener();
        this.bindViewChangeListener();

        //disabled dates
        this.cfg.beforeShowDay = function(date) {
            if(_self.cfg.preShowDay) {
                return _self.cfg.preShowDay(date);
            }
            else if(_self.cfg.disabledWeekends) {
                return $.datepicker.noWeekends(date);
            }
            else {
                return [true,''];
            }
        }

        //Setup timepicker
        var hasTimePicker = this.hasTimePicker();
        if(hasTimePicker) {
            this.configureTimePicker();
        }

        //Client behaviors, input skinning and z-index
        if(this.cfg.popup) {
            PrimeFaces.skinInput(this.jqEl);

            if(this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.jqEl, this.cfg.behaviors);
            }

            this.cfg.beforeShow = function(input, inst) {
                //display on top
                setTimeout(function() {
                    $('#ui-datepicker-div').css('z-index', ++PrimeFaces.zindex);
                }, 1);
                
                // touch support - prevents keyboard popup
                if(PrimeFaces.env.touch && !_self.input.attr("readonly") && _self.cfg.showOn && _self.cfg.showOn === 'button') {
                    $(this).prop("readonly", true);
                }
                
                //user callback
                var preShow = _self.cfg.preShow;
                if(preShow) {
                    return _self.cfg.preShow.call(_self, input, inst);
                }
            };
        }

        // touch support - prevents keyboard popup
        if (PrimeFaces.env.touch && !this.input.attr("readonly") && this.cfg.showOn && this.cfg.showOn === 'button') {
            this.cfg.onClose = function(dateText, inst) {
                $(this).attr("readonly", false);
            };
        }

        //Initialize calendar
        if(!this.cfg.disabled) {
            if(hasTimePicker) {
                if(this.cfg.timeOnly)
                    this.jqEl.timepicker(this.cfg);
                else
                    this.jqEl.datetimepicker(this.cfg);
            }
            else {
                this.jqEl.datepicker(this.cfg);
            }
        }

        //extensions
        if(this.cfg.popup && this.cfg.showOn) {
            var triggerButton = this.jqEl.siblings('.ui-datepicker-trigger:button');
            triggerButton.html('').addClass('ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only')
                        .append('<span class="ui-button-icon-left ui-icon ui-icon-calendar"></span><span class="ui-button-text">ui-button</span>');

            var title = this.jqEl.attr('title');
            if(title) {
                triggerButton.attr('title', title);
            }

            PrimeFaces.skinButton(triggerButton);
            $('#ui-datepicker-div').addClass('ui-shadow');
        }

        //mark target and descandants of target as a trigger for a primefaces overlay
        if(this.cfg.popup) {
            this.jq.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        if (this.cfg.mask) {
            var maskCfg = {placeholder:this.cfg.maskSlotChar||'_'};
            this.input.mask(this.cfg.mask, maskCfg);
        }
    },

    refresh: function(cfg) {
        if(cfg.popup && $.datepicker._lastInput && (cfg.id + '_input') === $.datepicker._lastInput.id) {
            $.datepicker._hideDatepicker();
        }

        this.init(cfg);
    },

    configureLocale: function() {
        var localeSettings = PrimeFaces.locales[this.cfg.locale];

        if(localeSettings) {
            for(var setting in localeSettings) {
                this.cfg[setting] = localeSettings[setting];
            }
        }
    },

    bindDateSelectListener: function() {
        var _self = this;

        this.cfg.onSelect = function() {
            if(_self.cfg.popup) {
                _self.fireDateSelectEvent();
            }
            else {
                var newDate = $.datepicker.formatDate(_self.cfg.dateFormat, _self.getDate());

                _self.input.val(newDate);
                _self.fireDateSelectEvent();
            }
        };
    },

    fireDateSelectEvent: function() {
        if(this.cfg.behaviors) {
            var dateSelectBehavior = this.cfg.behaviors['dateSelect'];

            if(dateSelectBehavior) {
                dateSelectBehavior.call(this);
            }
        }
    },

    bindViewChangeListener: function() {
        if(this.hasBehavior('viewChange')) {
            var $this = this;
            this.cfg.onChangeMonthYear = function(year, month) {
                $this.fireViewChangeEvent(year, month);
            };
        }
    },

    fireViewChangeEvent: function(year, month) {
        if(this.cfg.behaviors) {
            var viewChangeBehavior = this.cfg.behaviors['viewChange'];

            if(viewChangeBehavior) {
                var ext = {
                        params: [
                            {name: this.id + '_month', value: month},
                            {name: this.id + '_year', value: year}
                        ]
                };

                viewChangeBehavior.call(this, ext);
            }
        }
    },

    configureTimePicker: function() {
        var pattern = this.cfg.dateFormat,
        timeSeparatorIndex = pattern.toLowerCase().indexOf('h');

        this.cfg.dateFormat = pattern.substring(0, timeSeparatorIndex - 1);
        this.cfg.timeFormat = pattern.substring(timeSeparatorIndex, pattern.length);

        //second
        if(this.cfg.timeFormat.indexOf('ss') != -1) {
            this.cfg.showSecond = true;
        }

        //ampm
        if(this.cfg.timeFormat.indexOf('TT') != -1) {
            this.cfg.ampm = true;
        }

        //restrains
        if(this.cfg.minDate) {
            this.cfg.minDate = $.datepicker.parseDateTime(this.cfg.dateFormat, this.cfg.timeFormat, this.cfg.minDate, {}, {});
        }

        if(this.cfg.maxDate) {
            this.cfg.maxDate = $.datepicker.parseDateTime(this.cfg.dateFormat, this.cfg.timeFormat, this.cfg.maxDate, {}, {});
        }

        if(!this.cfg.showButtonPanel) {
            this.cfg.showButtonPanel = false;
        }
    },

    hasTimePicker: function() {
        return this.cfg.dateFormat.toLowerCase().indexOf('h') != -1;
    },

    setDate: function(date) {
        this.jqEl.datetimepicker('setDate', date);
    },

    getDate: function() {
        return this.jqEl.datetimepicker('getDate');
    },

    enable: function() {
        this.jqEl.datetimepicker('enable');
    },

    disable: function() {
        this.jqEl.datetimepicker('disable');
    },

    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }

        return false;
    }

});
/**
 * PrimeFaces Carousel Widget
 */
PrimeFaces.widget.Carousel = PrimeFaces.widget.DeferredWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.viewport = this.jq.children('.ui-carousel-viewport'); 
        this.itemsContainer = this.viewport.children('.ui-carousel-items');
        this.items = this.itemsContainer.children('li');
        this.itemsCount = this.items.length;
        this.header = this.jq.children('.ui-carousel-header');
        this.prevNav = this.header.children('.ui-carousel-prev-button');
        this.nextNav = this.header.children('.ui-carousel-next-button');
        this.pageLinks = this.header.find('> .ui-carousel-page-links > .ui-carousel-page-link');
        this.dropdown = this.header.children('.ui-carousel-dropdown');
        this.mobileDropdown = this.header.children('.ui-carousel-mobiledropdown');
        this.stateholder = $(this.jqId + '_page');
        
        this.cfg.numVisible = this.cfg.numVisible||3;
        this.cfg.firstVisible = this.cfg.firstVisible||0;
        this.columns = this.cfg.numVisible;
        this.first = this.cfg.firstVisible;
        this.cfg.effectDuration = this.cfg.effectDuration||500;
        this.cfg.circular = this.cfg.circular||false;
        this.cfg.breakpoint = this.cfg.breakpoint||640;
        this.page = parseInt(this.first/this.columns);
        this.totalPages = Math.ceil(this.itemsCount/this.cfg.numVisible);
        this.renderDeferred();
    },
    
    _render: function() {
        this.updateNavigators();
        this.bindEvents();
        
        if(this.cfg.responsive) {
            this.refreshDimensions();
        }
        else {
            this.calculateItemWidths(this.columns);
            this.jq.width(this.jq.width());
            this.updateNavigators();
        }
    },
    
    calculateItemWidths: function() {
        var firstItem = this.items.eq(0);
        if(firstItem.length) {
            var itemFrameWidth = firstItem.outerWidth(true) - firstItem.width();    //sum of margin, border and padding
            this.items.width((this.viewport.innerWidth() - itemFrameWidth * this.columns) / this.columns);
        }
    },
    
    refreshDimensions: function() {
        var win = $(window);
        if(win.width() <= this.cfg.breakpoint) {
            this.columns = 1;
            this.calculateItemWidths(this.columns);
            this.totalPages = this.itemsCount;
            this.mobileDropdown.show();
            this.pageLinks.hide();
        }
        else {
            this.columns = this.cfg.numVisible;
            this.calculateItemWidths();
            this.totalPages = Math.ceil(this.itemsCount / this.cfg.numVisible);
            this.mobileDropdown.hide();
            this.pageLinks.show();
        }
        
        this.page = parseInt(this.first / this.columns);
        this.updateNavigators();
        this.itemsContainer.css('left', (-1 * (this.viewport.innerWidth() * this.page)));
    },
    
    bindEvents: function() {
        var $this = this;
        
        this.prevNav.on('click', function() {
            if($this.page !== 0) {
                $this.setPage($this.page - 1);
            }
            else if($this.cfg.circular) {
                $this.setPage($this.totalPages - 1);
            }
        });
        
        this.nextNav.on('click', function() {
            var lastPage = ($this.page === ($this.totalPages - 1));
            
            if(!lastPage) {
                $this.setPage($this.page + 1);
            }
            else if($this.cfg.circular) {
                $this.setPage(0);
            }
        });
        
        this.itemsContainer.swipe({
            swipe:function(event, direction) {
                if(direction === 'left') {
                    if($this.page === ($this.totalPages - 1)) {
                        if($this.cfg.circular)
                            $this.setPage(0);
                    }
                    else {
                        $this.setPage($this.page + 1);
                    }
                }
                else if(direction === 'right') {
                    if($this.page === 0) {
                        if($this.cfg.circular)
                            $this.setPage($this.totalPages - 1);
                    }
                    else {
                        $this.setPage($this.page - 1);
                    }
                }
            }
        });
        
        if(this.pageLinks.length) {
            this.pageLinks.on('click', function(e) {
                $this.setPage($(this).index());
                e.preventDefault();
            });
        }
        
        this.header.children('select').on('change', function() {
            $this.setPage(parseInt($(this).val()) - 1);
        });
        
        if(this.cfg.autoplayInterval) {
            this.cfg.circular = true;
            this.startAutoplay();
        }
        
        if(this.cfg.responsive) {
            var resizeNS = 'resize.' + this.id;
            $(window).off(resizeNS).on(resizeNS, function() {
                $this.refreshDimensions();
            });
        }
    },
    
    updateNavigators: function() {
        if(!this.cfg.circular) {
            if(this.page === 0) {
                this.prevNav.addClass('ui-state-disabled');
                this.nextNav.removeClass('ui-state-disabled');   
            }
            else if(this.page === (this.totalPages - 1)) {
                this.prevNav.removeClass('ui-state-disabled');
                this.nextNav.addClass('ui-state-disabled');
            }
            else {
                this.prevNav.removeClass('ui-state-disabled');
                this.nextNav.removeClass('ui-state-disabled');   
            }
        }
        
        if(this.pageLinks.length) {
            this.pageLinks.filter('.ui-icon-radio-on').removeClass('ui-icon-radio-on');
            this.pageLinks.eq(this.page).addClass('ui-icon-radio-on');
        }
        
        if(this.dropdown.length) {
            this.dropdown.val(this.page + 1);
        }
        
        if(this.mobileDropdown.length) {
            this.mobileDropdown.val(this.page + 1);
        }
    },
    
    setPage: function(p) {      
        if(p !== this.page && !this.itemsContainer.is(':animated')) {
            var $this = this;
            
            this.itemsContainer.animate({
                left: -1 * (this.viewport.innerWidth() * p)
                ,easing: this.cfg.easing
            }, 
            {
                duration: this.cfg.effectDuration,
                easing: this.cfg.easing,
                complete: function() {
                    $this.page = p;
                    $this.first = $this.page * $this.columns;
                    $this.updateNavigators();
                    $this.stateholder.val($this.page);
                }
            });
        }
    },
    
    startAutoplay: function() {
        var $this = this;
        
        this.interval = setInterval(function() {
            if($this.page === ($this.totalPages - 1))
                $this.setPage(0);
            else
                $this.setPage($this.page + 1);
        }, this.cfg.autoplayInterval);
    },
    
    stopAutoplay: function() {
        clearInterval(this.interval);
    }
    
});  
          /**
 * PrimeFaces ColumnToggler Widget
 */
PrimeFaces.widget.ColumnToggler = PrimeFaces.widget.DeferredWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.table = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.datasource);
        this.trigger = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.trigger);
        this.tableId = this.table.attr('id');
        this.thead = $(PrimeFaces.escapeClientId(this.tableId) + '_head');
        this.tbody = $(PrimeFaces.escapeClientId(this.tableId) + '_data');
        this.tfoot = $(PrimeFaces.escapeClientId(this.tableId) + '_foot');
        this.visible = false;
        
        this.render();
        this.bindEvents();
    },
    
    render: function() {
        this.columns = this.thead.find('> tr > th:not(.ui-static-column)');
        this.panel = $('<div></div>').attr('id', this.cfg.id).addClass('ui-columntoggler ui-widget ui-widget-content ui-shadow ui-corner-all')
                .append('<ul class="ui-columntoggler-items"></ul').appendTo(document.body);
        this.itemContainer = this.panel.children('ul');
                
        for(var i = 0; i < this.columns.length; i++) {
            var column = this.columns.eq(i),
            hidden = column.hasClass('ui-helper-hidden'),
            boxClass = hidden ? 'ui-chkbox-box ui-widget ui-corner-all ui-state-default' : 'ui-chkbox-box ui-widget ui-corner-all ui-state-default ui-state-active',
            iconClass = (hidden) ? 'ui-chkbox-icon ui-icon ui-icon-blank' : 'ui-chkbox-icon ui-icon ui-icon-check';
                    
            $('<li class="ui-columntoggler-item">' + 
                    '<div class="ui-chkbox ui-widget">' +
                    '<div class="' + boxClass + '"><span class="' + iconClass + '"></span></div>' + 
                    '</div>'
                    + '<label>' + column.children('.ui-column-title').text() + '</label></li>').data('column', column.attr('id')).appendTo(this.itemContainer);
        }
        
        if(this.panel.outerHeight() > 200) {
            this.panel.height(200);
        }
        this.hide();
    },
    
    bindEvents: function() {
        var $this = this,
        hideNS = 'mousedown.' + this.id,
        resizeNS = 'resize.' + this.id;
        
        //trigger
        this.trigger.off('click.ui-columntoggler').on('click.ui-columntoggler', function(e) {
            if($this.visible)
                $this.hide();
            else
                $this.show();
        });
        
        //checkboxes
        this.itemContainer.find('> .ui-columntoggler-item > .ui-chkbox > .ui-chkbox-box').on('mouseover.columnToggler', function() {
                var item = $(this);
                if(!item.hasClass('ui-state-active')) {
                    item.addClass('ui-state-hover');
                }
            })
            .on('mouseout.columnToggler', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.columnToggler', function(e) {
                $this.toggle($(this));
                e.preventDefault();
            });
            
        //labels
        this.itemContainer.find('> .ui-columntoggler-item > label').on('click.selectCheckboxMenu', function(e) {
            $this.toggle($(this).prev().children('.ui-chkbox-box'));
            PrimeFaces.clearSelection();
            e.preventDefault();
        });
            
        //hide overlay when outside is clicked
        $(document.body).off(hideNS).on(hideNS, function (e) {        
            if(!$this.visible) {
                return;
            }

            //do nothing on trigger mousedown
            var target = $(e.target);
            if($this.trigger.is(target)||$this.trigger.has(target).length) {
                return;
            }

            //hide the panel and remove focus from label
            var offset = $this.panel.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + $this.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.panel.height()) {

                $this.hide();
            }
        });

        //Realign overlay on resize
        $(window).off(resizeNS).on(resizeNS, function() {
            if($this.visible) {
                $this.alignPanel();
            }
        });
    },
    
    toggle: function(chkbox) {
        if(chkbox.hasClass('ui-state-active')) {
            this.uncheck(chkbox);
        }
        else {
            this.check(chkbox);
        }
    },
    
    check: function(chkbox) {
        chkbox.addClass('ui-state-active').removeClass('ui-state-hover').children('.ui-chkbox-icon').addClass('ui-icon-check').removeClass('ui-icon-blank');
        
        var index = $(document.getElementById(chkbox.closest('li.ui-columntoggler-item').data('column'))).index() + 1,
        columnHeader = this.thead.children('tr').find('th:nth-child(' + index + ')');

        columnHeader.removeClass('ui-helper-hidden');
        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).removeClass('ui-helper-hidden');
        this.tbody.children('tr').find('td:nth-child(' + index + ')').removeClass('ui-helper-hidden');
        this.tfoot.children('tr').find('td:nth-child(' + index + ')').removeClass('ui-helper-hidden');
        
        this.fireToggleEvent(true, (index - 1));
    },
    
    uncheck: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
        
        var index = $(document.getElementById(chkbox.closest('li.ui-columntoggler-item').data('column'))).index() + 1,
        columnHeader = this.thead.children('tr').find('th:nth-child(' + index + ')');
        
        columnHeader.addClass('ui-helper-hidden');
        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).addClass('ui-helper-hidden');
        this.tbody.children('tr').find('td:nth-child(' + index + ')').addClass('ui-helper-hidden');
        this.tfoot.children('tr').find('td:nth-child(' + index + ')').addClass('ui-helper-hidden');

        this.fireToggleEvent(false, (index - 1));
    },
    
    alignPanel: function() {
        this.panel.css({'left':'', 'top':'', 'z-index': ++PrimeFaces.zindex}).position({
                            my: 'left top'
                            ,at: 'left bottom'
                            ,of: this.trigger
                        });
                        
        if(!this.widthAligned && (this.panel.width() < this.trigger.width())) {
            this.panel.width(this.trigger.width());
            this.widthAligned = true;
        }
    },
    
    show: function() {
        this.alignPanel();
        this.panel.show();
        this.visible = true;
    },
    
    hide: function() {
		this.panel.fadeOut('fast');
        this.visible = false;
    },
    
    fireToggleEvent: function(visible, index) {
        if(this.cfg.behaviors) {
            var toggleBehavior = this.cfg.behaviors['toggle'];

            if(toggleBehavior) {
                var visibility = visible ? 'VISIBLE' : 'HIDDEN',
                ext = {
                    params: [
                        {name: this.id + '_visibility', value: visibility},
                        {name: this.id + '_index', value: index}
                    ]
                };

                toggleBehavior.call(this, ext);
            }
        }
    }

});  
/**
 * PrimeFaces Dashboard Widget
 */
PrimeFaces.widget.Dashboard = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.connectWith =  this.jqId + ' .ui-dashboard-column';
        this.cfg.placeholder = 'ui-state-hover';
        this.cfg.forcePlaceholderSize = true;
        this.cfg.revert=false;
        this.cfg.handle='.ui-panel-titlebar';

        var _self = this;

        if(this.cfg.behaviors) {
            var reorderBehavior = this.cfg.behaviors['reorder'];

            if(reorderBehavior) {
                this.cfg.update = function(e, ui) {

                    if(this === ui.item.parent()[0]) {
                        var itemIndex = ui.item.parent().children().filter(':not(script):visible').index(ui.item),
                        receiverColumnIndex =  ui.item.parent().parent().children().index(ui.item.parent());

                        var ext = {
                            params: [
                                {name: _self.id + '_reordered', value: true},
                                {name: _self.id + '_widgetId', value: ui.item.attr('id')},
                                {name: _self.id + '_itemIndex', value: itemIndex},
                                {name: _self.id + '_receiverColumnIndex', value: receiverColumnIndex}
                            ]
                        }  

                        if(ui.sender) {
                            ext.params.push({name: _self.id + '_senderColumnIndex', value: ui.sender.parent().children().index(ui.sender)});
                        }

                        reorderBehavior.call(_self, ext);
                    }

                };
            }
        } 

        $(this.jqId + ' .ui-dashboard-column').sortable(this.cfg);
    }
    
});
/**
 * PrimeFaces DataGrid Widget
 */
PrimeFaces.widget.DataGrid = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.formId = $(this.jqId).closest('form').attr('id');
        this.content = $(this.jqId + '_content');

        if(this.cfg.paginator) {
            this.setupPaginator();
        }
    },
    
    setupPaginator: function() {
        var $this = this;
        this.cfg.paginator.paginate = function(newState) {
            $this.handlePagination(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
    },
            
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }
    
        return false;
    },
            
    handlePagination: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            params: [
                {name: this.id + '_pagination', value: true},
                {name: this.id + '_first', value: newState.first},
                {name: this.id + '_rows', value: newState.rows}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.paginator.cfg.page = newState.page;
                $this.paginator.updateUI();
            }
        };

        if(this.hasBehavior('page')) {
            var pageBehavior = this.cfg.behaviors['page'];
            pageBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
    
    getPaginator: function() {
        return this.paginator;
    }
    
});
/**
 * PrimeFaces DataList Widget
 */
PrimeFaces.widget.DataList = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.formId = $(this.jqId).parents('form:first').attr('id');
        this.content = $(this.jqId + '_content');

        if(this.cfg.paginator) {
            this.setupPaginator();
        }
    },
    
    setupPaginator: function() {
        var $this = this;
        this.cfg.paginator.paginate = function(newState) {
            $this.handlePagination(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
    },
    
    handlePagination: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            params: [
                {name: this.id + '_pagination', value: true},
                {name: this.id + '_first', value: newState.first},
                {name: this.id + '_rows', value: newState.rows}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.paginator.cfg.page = newState.page;
                $this.paginator.updateUI();
            }
        };

        if(this.hasBehavior('page')) {
            var pageBehavior = this.cfg.behaviors['page'];
            pageBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
    
    getPaginator: function() {
        return this.paginator;
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }
    
        return false;
    }
    
});
/**
 * PrimeFaces DataScroller Widget
 */
PrimeFaces.widget.DataScroller = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.content = this.jq.children('div.ui-datascroller-content');
        this.list = this.content.children('ul');
        this.loaderContainer = this.content.children('div.ui-datascroller-loader');
        this.loadStatus = $('<div class="ui-datascroller-loading"></div>');
        this.loading = false;
        this.allLoaded = false;
        this.cfg.offset = 0;
        this.cfg.mode = this.cfg.mode||'document';
        this.cfg.buffer = (100 - this.cfg.buffer) / 100;
                
        if(this.cfg.loadEvent === 'scroll') {
            this.bindScrollListener();
        }
        else {
            this.loadTrigger = this.loaderContainer.children();
            this.bindManualLoader();
        }
    },
    
    bindScrollListener: function() {
        var $this = this;
        
        if(this.cfg.mode === 'document') {
            var win = $(window),
            doc = $(document),
            $this = this,
            NS = 'scroll.' + this.id;

            win.off(NS).on(NS, function () {
                if(win.scrollTop() >= ((doc.height() * $this.cfg.buffer) - win.height()) && $this.shouldLoad()) {
                    $this.load();
                }
            });
        }
        else {
            this.content.on('scroll', function () {
                var scrollTop = this.scrollTop,
                scrollHeight = this.scrollHeight,
                viewportHeight = this.clientHeight;

                if((scrollTop >= ((scrollHeight * $this.cfg.buffer) - (viewportHeight))) && $this.shouldLoad()) {
                    $this.load();
                }
            });
        }
    },
    
    bindManualLoader: function() {
        var $this = this;
        
        this.loadTrigger.on('click.dataScroller', function(e) {
            $this.load();
            e.preventDefault();
        });
    },
    
    load: function() {
        this.loading = true;
        this.cfg.offset += this.cfg.chunkSize;
        
        this.loadStatus.appendTo(this.loaderContainer);
        if(this.loadTrigger) {
            this.loadTrigger.hide();
        }
                        
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            global: false,
            params: [{name: this.id + '_load', value: true},{name: this.id + '_offset', value: this.cfg.offset}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        this.list.append(content);
                    }
                });
                
                return true;
            },
            oncomplete: function() {
                $this.loading = false;
                $this.allLoaded = ($this.cfg.offset + $this.cfg.chunkSize) >= $this.cfg.totalSize;
                
                $this.loadStatus.remove();
                
                if($this.loadTrigger && !$this.allLoaded) {
                    $this.loadTrigger.show();
                }
            }
        };
        
        PrimeFaces.ajax.AjaxRequest(options);
    },
    
    shouldLoad: function() {
        return (!this.loading && !this.allLoaded);
    }
    
});
 /**
 * PrimeFaces DataTable Widget
 */
PrimeFaces.widget.DataTable = PrimeFaces.widget.DeferredWidget.extend({
    
    SORT_ORDER: {
        ASCENDING: 1,
        DESCENDING: -1,
        UNSORTED: 0
    },
    
    init: function(cfg) {
        this._super(cfg);
        
        this.thead = this.getThead(); 
        this.tbody = this.getTbody();
        this.tfoot = this.getTfoot();
        
        if(this.cfg.paginator) {
            this.bindPaginator();
        }

        this.bindSortEvents();
        
        if(this.cfg.rowHover) {
            this.setupRowHover();
        }

        if(this.cfg.selectionMode) {
            this.setupSelection();
        }

        if(this.cfg.filter) {
            this.setupFiltering();
        }

        if(this.cfg.expansion) {
            this.expansionProcess = [];
            this.bindExpansionEvents();
        }

        if(this.cfg.editable) {
            this.bindEditEvents();
        }

        if(this.cfg.draggableRows) {
            this.makeRowsDraggable();
        }
        
        if(this.cfg.reflow) {
            this.initReflow();
        }
        
        this.renderDeferred();
    },
    
    _render: function() {
        if(this.cfg.scrollable) {
            this.setupScrolling();
        }

        if(this.cfg.resizableColumns) {
            this.setupResizableColumns();
        }

        if(this.cfg.draggableColumns) {
            this.setupDraggableColumns();
        }

        if(this.cfg.stickyHeader) {
            this.setupStickyHeader();
        }
    },
    
    getThead: function() {
        return $(this.jqId + '_head');
    },
    
    getTbody: function() {
        return $(this.jqId + '_data');
    },
    
    getTfoot: function() {
        return $(this.jqId + '_foot');
    },
        
    updateData: function(data, clear) {
        var empty = (clear === undefined) ? true: clear;
        
        if(empty)
            this.tbody.html(data);
        else
            this.tbody.append(data);
        
        this.postUpdateData();
    },
    
    postUpdateData: function() {
        if(this.cfg.draggableRows) {
            this.makeRowsDraggable();
        } 
        
        if(this.cfg.reflow) {
            this.initReflow();
        }
    },
 
    /**
     * @Override
     */
    refresh: function(cfg) {        
        this.columnWidthsFixed = false;
        
        this.init(cfg);
    },
    
    /**
     * Binds the change event listener and renders the paginator
     */
    bindPaginator: function() {
        var _self = this;
        this.cfg.paginator.paginate = function(newState) {
            _self.paginate(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
    },
    
    /**
     * Applies events related to sorting in a non-obstrusive way
     */
    bindSortEvents: function() {
        var $this = this;
        this.cfg.tabindex = this.cfg.tabindex||'0';
        this.sortableColumns = this.thead.find('> tr > th.ui-sortable-column');
        this.sortableColumns.attr('tabindex', this.cfg.tabindex);
        
        if(this.cfg.multiSort) {
            this.sortMeta = [];
        }

        for(var i = 0; i < this.sortableColumns.length; i++) {
            var columnHeader = this.sortableColumns.eq(i),
            sortIcon = columnHeader.children('span.ui-sortable-column-icon'),
            sortOrder = null;
    
            if(columnHeader.hasClass('ui-state-active')) {
                if(sortIcon.hasClass('ui-icon-triangle-1-n'))
                    sortOrder = this.SORT_ORDER.ASCENDING;
                else
                    sortOrder = this.SORT_ORDER.DESCENDING;
                
                if($this.cfg.multiSort) {
                    $this.addSortMeta({
                        col: columnHeader.attr('id'), 
                        order: sortOrder
                    });
                }
            }
            else {
                sortOrder = this.SORT_ORDER.UNSORTED;
            }
            
            columnHeader.data('sortorder', sortOrder);
        }
        
        this.sortableColumns.on('mouseenter.dataTable', function() {
            var column = $(this);
            
            if(!column.hasClass('ui-state-active'))
                column.addClass('ui-state-hover');
        })
        .on('mouseleave.dataTable', function() {
            var column = $(this);
            
            if(!column.hasClass('ui-state-active'))
                column.removeClass('ui-state-hover');
        })
        .on('blur.dataTable', function() {
            $(this).removeClass('ui-state-focus');
        })
        .on('focus.dataTable', function() {
            $(this).addClass('ui-state-focus');
        })
        .on('keydown.dataTable', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER) && $(e.target).is(':not(:input)')) {
                $(this).trigger('click.dataTable', (e.metaKey||e.ctrlKey));
                e.preventDefault();
            }
        })
        .on('click.dataTable', function(e, metaKeyOn) {
            if(!$this.shouldSort(e, this)) {
                return;
            }

            PrimeFaces.clearSelection();
                            
            var columnHeader = $(this),
            sortOrderData = columnHeader.data('sortorder'),
            sortOrder = (sortOrderData === $this.SORT_ORDER.UNSORTED) ? $this.SORT_ORDER.ASCENDING : -1 * sortOrderData,
            metaKey = e.metaKey||e.ctrlKey||metaKeyOn;
            
            if($this.cfg.multiSort) {
                if(metaKey) {
                    $this.addSortMeta({
                        col: columnHeader.attr('id'), 
                        order: sortOrder
                    });
                    $this.sort(columnHeader, sortOrder, true);
                }
                else {                        
                    $this.sortMeta = [];
                    $this.addSortMeta({
                        col: columnHeader.attr('id'), 
                        order: sortOrder
                    });
                    $this.sort(columnHeader, sortOrder);
                }
            }
            else {
                $this.sort(columnHeader, sortOrder);
            }

        });
    },
    
    shouldSort: function(event, column) {
        if(this.isEmpty()) {
            return false;
        }
        
        var target = $(event.target);
        if(target.closest('.ui-column-customfilter', column).length) {
            return false;
        }
        
        return target.is('th,span');
    },
    
    addSortMeta: function(meta) {
        this.sortMeta = $.grep(this.sortMeta, function(value) {
            return value.col !== meta.col;
        });
        
        this.sortMeta.push(meta);
    },
      
    /**
     * Binds filter events to standard filters
     */
    setupFiltering: function() {
        var $this = this,
        filterColumns = this.thead.find('> tr > th.ui-filter-column');
        this.cfg.filterEvent = this.cfg.filterEvent||'keyup';
        this.cfg.filterDelay = this.cfg.filterDelay||300;

        filterColumns.children('.ui-column-filter').each(function() {
            var filter = $(this);

            if(filter.is('input:text')) {
                PrimeFaces.skinInput(filter);
                $this.bindTextFilter(filter);
            } 
            else {
                PrimeFaces.skinSelect(filter);
                $this.bindChangeFilter(filter);
            }
        });
    },
    
    bindTextFilter: function(filter) {
        if(this.cfg.filterEvent === 'enter')
            this.bindEnterKeyFilter(filter);
        else
            this.bindFilterEvent(filter);
    },
    
    bindChangeFilter: function(filter) {
        var $this = this;
        
        filter.change(function() {
            $this.filter();
        });
    },
    
    bindEnterKeyFilter: function(filter) {
        var $this = this;
    
        filter.bind('keydown', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
                e.preventDefault();
            }
        }).bind('keyup', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
                $this.filter();

                e.preventDefault();
            }
        });
    },
    
    bindFilterEvent: function(filter) {
        var $this = this;
        
        //prevent form submit on enter key
        filter.on('keydown.dataTable-blockenter', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
                e.preventDefault();
            }
        })
        .on(this.cfg.filterEvent + '.dataTable', function(e) {
            if($this.filterTimeout) {
                clearTimeout($this.filterTimeout);
            }

            $this.filterTimeout = setTimeout(function() {
                $this.filter();
                $this.filterTimeout = null;
            },
            $this.cfg.filterDelay);
        });
    },
    
    setupRowHover: function() {
        var selector = '> tr.ui-widget-content';
        if(!this.cfg.selectionMode) {
            this.bindRowHover(selector);
        }
    },
    
    setupSelection: function() {
        this.selectionHolder = this.jqId + '_selection';
        this.cfg.rowSelectMode = this.cfg.rowSelectMode||'new';
        this.rowSelector = '> tr.ui-widget-content.ui-datatable-selectable';
        this.cfg.disabledTextSelection = this.cfg.disabledTextSelection === false ? false : true;

        var preselection = $(this.selectionHolder).val();
        this.selection = (preselection === "") ? [] : preselection.split(',');

        //shift key based range selection
        this.originRowIndex = 0;
        this.cursorIndex = null;

        this.bindSelectionEvents();
    },
    
    /**
     * Applies events related to selection in a non-obstrusive way
     */
    bindSelectionEvents: function() {
        if(this.cfg.selectionMode === 'radio') {
            this.bindRadioEvents();
        }
        else if(this.cfg.selectionMode === 'checkbox') {
            this.bindCheckboxEvents();
            this.updateHeaderCheckbox();
            
            if(this.cfg.rowSelectMode !== 'checkbox') {
                this.bindRowEvents();
            }
        }
        else {
            this.bindRowEvents();
        }
    },
    
    bindRowEvents: function() {
        var $this = this;

        this.bindRowHover(this.rowSelector);

        this.tbody.off('click.dataTable', this.rowSelector).on('click.dataTable', this.rowSelector, null, function(e) {
            $this.onRowClick(e, this);
        });
        
        //double click
        if(this.hasBehavior('rowDblselect')) {
            this.tbody.off('dblclick.dataTable', this.rowSelector).on('dblclick.dataTable', this.rowSelector, null, function(e) {
                $this.onRowDblclick(e, $(this));
            });
        };
    },
    
    bindRowHover: function(selector) {
        this.tbody.off('mouseenter.dataTable mouseleave.dataTable', selector)
                    .on('mouseenter.dataTable', selector, null, function() {
                        var element = $(this);

                        if(!element.hasClass('ui-state-highlight')) {
                            element.addClass('ui-state-hover');
                        }
                    })
                    .on('mouseleave.dataTable', selector, null, function() {
                        var element = $(this);

                        if(!element.hasClass('ui-state-highlight')) {
                            element.removeClass('ui-state-hover');
                        }
                    });
    },
    
    bindRadioEvents: function() {
        var $this = this,
        radioInputSelector = '> tr.ui-widget-content:not(.ui-datatable-empty-message) > td.ui-selection-column :radio';
        
        if(this.cfg.nativeElements) {            
            this.tbody.off('click.dataTable', radioInputSelector).on('click.dataTable', radioInputSelector, null, function(e) {
                var radioButton = $(this);

                if(!radioButton.prop('checked'))
                    $this.selectRowWithRadio(radioButton);
            });
        }
        else {
            var radioSelector = '> tr.ui-widget-content:not(.ui-datatable-empty-message) > td.ui-selection-column .ui-radiobutton .ui-radiobutton-box';
            this.tbody.off('click.dataTable mouseover.dataTable mouseout.dataTable', radioSelector)
                .on('mouseover.dataTable', radioSelector, null, function() {
                    var radio = $(this);
                    if(!radio.hasClass('ui-state-disabled')&&!radio.hasClass('ui-state-active')) {
                        radio.addClass('ui-state-hover');
                    }
                })
                .on('mouseout.dataTable', radioSelector, null, function() {
                    var radio = $(this);
                    radio.removeClass('ui-state-hover');
                })
                .on('click.dataTable', radioSelector, null, function() {
                    var radio = $(this),
                    checked = radio.hasClass('ui-state-active'),
                    disabled = radio.hasClass('ui-state-disabled');

                    if(!disabled && !checked) {
                        $this.selectRowWithRadio(radio);
                    }
                });
        }
                       
        //keyboard support
        this.tbody.off('focus.dataTable blur.dataTable change.dataTable', radioInputSelector)
            .on('focus.dataTable', radioInputSelector, null, function() {
                var input = $(this),
                box = input.parent().next();

                if(input.prop('checked')) {
                    box.removeClass('ui-state-active');
                }

                box.addClass('ui-state-focus');
            })
            .on('blur.dataTable', radioInputSelector, null, function() {
                var input = $(this),
                box = input.parent().next();

                if(input.prop('checked')) {
                    box.addClass('ui-state-active');
                }

                box.removeClass('ui-state-focus');
            })
            .on('change.dataTable', radioInputSelector, null, function() {
                var currentInput = $this.tbody.find(radioInputSelector).filter(':checked'),
                currentRadio = currentInput.parent().next();

                $this.selectRowWithRadio(currentRadio);
            });
                      
    },
    
    bindCheckboxEvents: function() {
        var $this = this,
        checkboxInputSelector = '> tr.ui-widget-content.ui-datatable-selectable > td.ui-selection-column :checkbox';

        if(this.cfg.nativeElements) {
            this.checkAllToggler = this.thead.find('> tr > th.ui-selection-column > :checkbox');
            
            this.checkAllToggler.on('click', function() {
                $this.toggleCheckAll();
            });
            
            this.tbody.off('click.dataTable', checkboxInputSelector).on('click.dataTable', checkboxInputSelector, null, function(e) {
                var checkbox = $(this);

                if(checkbox.prop('checked'))
                    $this.selectRowWithCheckbox(checkbox);
                else                      
                    $this.unselectRowWithCheckbox(checkbox);
            });
        }
        else {
            this.checkAllToggler = this.thead.find('> tr > th.ui-selection-column > .ui-chkbox.ui-chkbox-all > .ui-chkbox-box');
            
            this.checkAllToggler.on('mouseover', function() {
                var box = $(this);
                if(!box.hasClass('ui-state-disabled')&&!box.hasClass('ui-state-active')) {
                    box.addClass('ui-state-hover');
                }
            })
            .on('mouseout', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click', function() {
                var box = $(this);
                if(!box.hasClass('ui-state-disabled')) {
                    $this.toggleCheckAll();
                }
            });
            
            var checkboxSelector = '> tr.ui-widget-content.ui-datatable-selectable > td.ui-selection-column .ui-chkbox .ui-chkbox-box';
            this.tbody.off('mouseover.dataTable mouseover.dataTable click.dataTable', checkboxSelector)
                        .on('mouseover.dataTable', checkboxSelector, null, function() {
                            var box = $(this);
                            if(!box.hasClass('ui-state-active')) {
                                box.addClass('ui-state-hover');
                            }
                        })
                        .on('mouseout.dataTable', checkboxSelector, null, function() {
                            $(this).removeClass('ui-state-hover');
                        })
                        .on('click.dataTable', checkboxSelector, null, function() {
                            var checkbox = $(this),
                            checked = checkbox.hasClass('ui-state-active');

                            if(checked)
                                $this.unselectRowWithCheckbox(checkbox);
                            else                      
                                $this.selectRowWithCheckbox(checkbox);
                        });
        }
        
        //keyboard support
        this.tbody.off('focus.dataTable blur.dataTable keydown.dataTable keyup.dataTable', checkboxInputSelector)
                    .on('focus.dataTable', checkboxInputSelector, null, function() {
                        var input = $(this),
                        box = input.parent().next();

                        if(input.prop('checked')) {
                            box.removeClass('ui-state-active');
                        }

                        box.addClass('ui-state-focus');
                    })
                    .on('blur.dataTable', checkboxInputSelector, null, function() {
                        var input = $(this),
                        box = input.parent().next();

                        if(input.prop('checked')) {
                            box.addClass('ui-state-active');
                        }

                        box.removeClass('ui-state-focus');
                    })
                    .on('keydown.dataTable', checkboxInputSelector, null, function(e) {
                        var keyCode = $.ui.keyCode;
                        if(e.which === keyCode.SPACE) {
                            e.preventDefault();
                        }
                    })
                    .on('keyup.dataTable', checkboxInputSelector, null, function(e) {
                        var keyCode = $.ui.keyCode;

                        if(e.which === keyCode.SPACE) {
                            var input = $(this),
                            box = input.parent().next();

                            if(input.prop('checked')) {
                                $this.unselectRowWithCheckbox(box);
                            } 
                            else {                        
                                $this.selectRowWithCheckbox(box);
                            }

                            e.preventDefault();
                        }
                    });
    },
    
    /**
     * Applies events related to row expansion in a non-obstrusive way
     */
    bindExpansionEvents: function() {
        var $this = this,
        togglerSelector = '> tr > td > div.ui-row-toggler';
        
        this.tbody.off('click.datatable-expansion', togglerSelector)
                    .on('click.datatable-expansion', togglerSelector, null, function() {
                        $this.toggleExpansion($(this));
                    });
    },
    
    initReflow: function() {
        var headerColumns = this.thead.find('> tr > th');
        
        for(var i = 0; i < headerColumns.length; i++) {
            var headerColumn = headerColumns.eq(i),
            title = headerColumn.children('.ui-column-title').text();
            this.tbody.find('> tr > td:nth-child(' + (i + 1) + ')').prepend('<span class="ui-column-title">' + title + '</span>');
        }
    },
    
    setupScrolling: function() {
        this.scrollHeader = this.jq.children('.ui-datatable-scrollable-header');
        this.scrollBody = this.jq.children('.ui-datatable-scrollable-body');
        this.scrollFooter = this.jq.children('.ui-datatable-scrollable-footer');
        this.scrollStateHolder = $(this.jqId + '_scrollState');
        this.scrollHeaderBox = this.scrollHeader.children('div.ui-datatable-scrollable-header-box');
        this.scrollFooterBox = this.scrollFooter.children('div.ui-datatable-scrollable-footer-box');
        this.headerTable = this.scrollHeaderBox.children('table');
        this.bodyTable = this.scrollBody.children('table');
        this.footerTable = this.scrollFooter.children('table');
        this.footerCols = this.scrollFooter.find('> .ui-datatable-scrollable-footer-box > table > tfoot > tr > td');
        this.percentageScrollHeight = this.cfg.scrollHeight && (this.cfg.scrollHeight.indexOf('%') !== -1);
        this.percentageScrollWidth = this.cfg.scrollWidth && (this.cfg.scrollWidth.indexOf('%') !== -1);
        var $this = this,
        scrollBarWidth = this.getScrollbarWidth() + 'px';
        
        if(this.cfg.scrollHeight) {
            if(this.percentageScrollHeight) {
                this.adjustScrollHeight();
            }
            
            if(this.hasVerticalOverflow()) {
                this.scrollHeaderBox.css('margin-right', scrollBarWidth);
                this.scrollFooterBox.css('margin-right', scrollBarWidth);
            }
        }
                
        this.fixColumnWidths();
                
        if(this.cfg.scrollWidth) {
            if(this.percentageScrollWidth)
                this.adjustScrollWidth();
            else
                this.setScrollWidth(parseInt(this.cfg.scrollWidth));
        }
        
        this.cloneHead();
              
        this.restoreScrollState();

        if(this.cfg.liveScroll) {
            this.scrollOffset = 0;
            this.cfg.liveScrollBuffer = (100 - this.cfg.liveScrollBuffer) / 100;
            this.shouldLiveScroll = true;       
            this.loadingLiveScroll = false;
            this.allLoadedLiveScroll = $this.cfg.scrollStep >= $this.cfg.scrollLimit;      
        }
        
        this.scrollBody.on('scroll.dataTable', function() {
            var scrollLeft = $this.scrollBody.scrollLeft();
            $this.scrollHeaderBox.css('margin-left', -scrollLeft);
            $this.scrollFooterBox.css('margin-left', -scrollLeft);

            if($this.shouldLiveScroll) {
                var scrollTop = this.scrollTop,
                scrollHeight = this.scrollHeight,
                viewportHeight = this.clientHeight;

                if((scrollTop >= ((scrollHeight * $this.cfg.liveScrollBuffer) - (viewportHeight))) && $this.shouldLoadLiveScroll()) {
                    $this.loadLiveRows();
                }
            }
            
            $this.saveScrollState();
        });
        
        this.scrollHeader.on('scroll.dataTable', function() {
            $this.scrollHeader.scrollLeft(0);
        });
        
        this.scrollFooter.on('scroll.dataTable', function() {
            $this.scrollFooter.scrollLeft(0);
        });
        
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.jq.is(':visible')) {
                if($this.percentageScrollHeight)
                    $this.adjustScrollHeight();
                
                if($this.percentageScrollWidth)
                    $this.adjustScrollWidth();
            }
        });
    },
    
    shouldLoadLiveScroll: function() {
        return (!this.loadingLiveScroll && !this.allLoadedLiveScroll);
    },
            
    cloneHead: function() {
        this.theadClone = this.thead.clone();
        this.theadClone.find('th').each(function() {
            var header = $(this);
            header.attr('id', header.attr('id') + '_clone');
            $(this).children().not('.ui-column-title').remove();
        });
        this.theadClone.removeAttr('id').addClass('ui-datatable-scrollable-theadclone').height(0).prependTo(this.bodyTable);
        
        //align horizontal scroller on keyboard tab
        if(this.cfg.scrollWidth) {
            this.sortableColumns.attr('tabindex', "-1").off('blur.dataTable focus.dataTable keydown.dataTable');
            
            var clonedSortableColumns = this.theadClone.find('> tr > th.ui-sortable-column');
            clonedSortableColumns.each(function() {
                $(this).data('original', $(this).attr('id').split('_clone')[0]);
            });
                        
            clonedSortableColumns.on('blur.dataTable', function() {
                $(PrimeFaces.escapeClientId($(this).data('original'))).removeClass('ui-state-focus');
            })
            .on('focus.dataTable', function() {
                $(PrimeFaces.escapeClientId($(this).data('original'))).addClass('ui-state-focus');
            })
            .on('keydown.dataTable', function(e) {
                var key = e.which,
                keyCode = $.ui.keyCode;

                if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER) && $(e.target).is(':not(:input)')) {
                    $(PrimeFaces.escapeClientId($(this).data('original'))).trigger('click.dataTable', (e.metaKey||e.ctrlKey));
                    e.preventDefault();
                }
            });
        }
    },
            
    adjustScrollHeight: function() {
        var relativeHeight = this.jq.parent().innerHeight() * (parseInt(this.cfg.scrollHeight) / 100),
        tableHeaderHeight = this.jq.children('.ui-datatable-header').outerHeight(true),
        tableFooterHeight = this.jq.children('.ui-datatable-footer').outerHeight(true),
        scrollersHeight = (this.scrollHeader.outerHeight(true) + this.scrollFooter.outerHeight(true)),
        paginatorsHeight = this.paginator ? this.paginator.getContainerHeight(true) : 0,
        height = (relativeHeight - (scrollersHeight + paginatorsHeight + tableHeaderHeight + tableFooterHeight));
        
        this.scrollBody.height(height);
    },
            
    adjustScrollWidth: function() {
        var width = parseInt((this.jq.parent().innerWidth() * (parseInt(this.cfg.scrollWidth) / 100)));
        this.setScrollWidth(width);
    },
    
    setOuterWidth: function(element, width) {
        var diff = element.outerWidth() - element.width();
        element.width(width - diff);
    },
            
    setScrollWidth: function(width) {
        var $this = this;
        this.jq.children('.ui-widget-header').each(function() {
            $this.setOuterWidth($(this), width);
        });
        this.scrollHeader.width(width);
        this.scrollBody.css('margin-right', 0).width(width);
        this.scrollFooter.width(width);
    },
    
    alignScrollBody: function() {
        var marginRight = this.hasVerticalOverflow() ? this.getScrollbarWidth() + 'px' : '0px';
        
        this.scrollHeaderBox.css('margin-right', marginRight);
        this.scrollFooterBox.css('margin-right', marginRight);
    },
    
    getScrollbarWidth: function() {
        if(!this.scrollbarWidth) {
            this.scrollbarWidth = PrimeFaces.env.browser.webkit ? '15' : PrimeFaces.calculateScrollbarWidth();
        }
        
        return this.scrollbarWidth;
    },
        
    hasVerticalOverflow: function() {
        return (this.cfg.scrollHeight && this.bodyTable.outerHeight() > this.scrollBody.outerHeight())
    },
    
    restoreScrollState: function() {
        var scrollState = this.scrollStateHolder.val(),
        scrollValues = scrollState.split(',');

        this.scrollBody.scrollLeft(scrollValues[0]);
        this.scrollBody.scrollTop(scrollValues[1]);
    },
    
    saveScrollState: function() {
        var scrollState = this.scrollBody.scrollLeft() + ',' + this.scrollBody.scrollTop();
        
        this.scrollStateHolder.val(scrollState);
    },
    
    clearScrollState: function() {
        this.scrollStateHolder.val('0,0');
    },
    
    fixColumnWidths: function() {
        var $this = this;
        
        if(!this.columnWidthsFixed) {
            if(PrimeFaces.isIE(7)) {
                this.bodyTable.css('width', 'auto');
            }
            
            if(this.cfg.scrollable) {
                this.scrollHeader.find('> .ui-datatable-scrollable-header-box > table > thead > tr > th').each(function() {
                    var headerCol = $(this),
                    colIndex = headerCol.index(),
                    width = headerCol.width();

                    headerCol.width(width);

                    if($this.footerCols.length > 0) {
                        var footerCol = $this.footerCols.eq(colIndex);
                        footerCol.width(width);
                    }
                });
            }
            else {
                this.jq.find('> .ui-datatable-tablewrapper > table > thead > tr > th').each(function() {
                    var col = $(this);
                    col.width(col.width());
                });
            }

            this.columnWidthsFixed = true;
        }
    },
    
    /**
     * Loads rows on-the-fly when scrolling live
     */
    loadLiveRows: function() {
        if(this.liveScrollActive) {
            return;
        }
        
        this.liveScrollActive = true;
        this.scrollOffset += this.cfg.scrollStep;

        //Disable scroll if there is no more data left
        if(this.scrollOffset === this.cfg.scrollLimit) {
            this.shouldLiveScroll = false;
        }
        
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId,
            params: [{name: this.id + '_scrolling', value: true},
                            {name: this.id + '_skipChildren', value: true},
                            {name: this.id + '_scrollOffset', value: this.scrollOffset},
                            {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        //insert new rows
                        this.updateData(content, false);
 
                        this.liveScrollActive = false;
                    }
                });

                return true;
            },
            oncomplete: function() {
                $this.loadingLiveScroll = false;
                $this.allLoadedLiveScroll = ($this.scrollOffset + $this.cfg.scrollStep) >= $this.cfg.scrollLimit;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },
    
    /**
     * Ajax pagination
     */
    paginate: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            params: [{name: this.id + '_pagination', value: true},
                    {name: this.id + '_first', value: newState.first},
                    {name: this.id + '_rows', value: newState.rows},
                    {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.updateData(content);
                            
                            if(this.checkAllToggler) {
                                this.updateHeaderCheckbox();
                            }
                            
                            if(this.cfg.scrollable) {
                                this.alignScrollBody();
                            }
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.paginator.cfg.page = newState.page;
                $this.paginator.updateUI();
            }
            
        };

        if(this.hasBehavior('page')) {
            var pageBehavior = this.cfg.behaviors['page'];

            pageBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.Request.handle(options); 
        }
    },
    
    /**
     * Ajax sort
     */
    sort: function(columnHeader, order, multi) {  
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            params: [{name: this.id + '_sorting', value: true},
                     {name: this.id + '_skipChildren', value: true},
                     {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.updateData(content);
                            
                            var paginator = $this.getPaginator();
                            if(paginator) {
                                paginator.setPage(0, true);
                            }
                            
                            if(!multi) {
                                this.sortableColumns.filter('.ui-state-active').data('sortorder', this.SORT_ORDER.UNSORTED).removeClass('ui-state-active')
                                            .find('.ui-sortable-column-icon').removeClass('ui-icon-triangle-1-n ui-icon-triangle-1-s');
                            }

                            columnHeader.data('sortorder', order).removeClass('ui-state-hover').addClass('ui-state-active');
                            var sortIcon = columnHeader.find('.ui-sortable-column-icon');
                            if(order === this.SORT_ORDER.DESCENDING) {
                                sortIcon.removeClass('ui-icon-triangle-1-n').addClass('ui-icon-triangle-1-s');
                            } else if(order === this.SORT_ORDER.ASCENDING) {
                                sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-n');
                            }
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args) {
                var paginator = $this.getPaginator();             
                if(paginator && args && paginator.cfg.rowCount !== args.totalRecords) {
                    paginator.setTotalRecords(args.totalRecords);
                }
            }
        };
        
        if(multi) {
            options.params.push({name: this.id + '_multiSorting', value: true});
            options.params.push({name: this.id + '_sortKey', value: $this.joinSortMetaOption('col')});
            options.params.push({name: this.id + '_sortDir', value: $this.joinSortMetaOption('order')});
        }
        else {
            options.params.push({name: this.id + '_sortKey', value: columnHeader.attr('id')});
            options.params.push({name: this.id + '_sortDir', value: order});
        }

        if(this.hasBehavior('sort')) {
            var sortBehavior = this.cfg.behaviors['sort'];

            sortBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.Request.handle(options); 
        }
    },
    
    joinSortMetaOption: function(option) {
        var value = '';
        
        for(var i = 0; i < this.sortMeta.length; i++) {
            value += this.sortMeta[i][option];
            
            if(i !== (this.sortMeta.length - 1)) {
                value += ',';
            }
        }
        
        return value;
    },
        
    /**
     * Ajax filter
     */
    filter: function() {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            params: [{name: this.id + '_filtering', value: true},
                     {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.updateData(content);

                            if(this.cfg.scrollable) {
                                this.alignScrollBody();
                            }

                            if(this.isCheckboxSelectionEnabled()) {
                                this.updateHeaderCheckbox();
                            }
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args) {
                var paginator = $this.getPaginator();
                if(paginator) {
                    paginator.setTotalRecords(args.totalRecords);
                }
            }
        };

        if(this.hasBehavior('filter')) {
            var filterBehavior = this.cfg.behaviors['filter'];

            filterBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.AjaxRequest(options); 
        }
    },
    
    onRowClick: function(event, rowElement, silent) {    
        //Check if rowclick triggered this event not a clickable element in row content
        if($(event.target).is('td:not(.ui-column-unselectable),span:not(.ui-c)')) {
            var row = $(rowElement),
            selected = row.hasClass('ui-state-highlight'),
            metaKey = event.metaKey||event.ctrlKey,
            shiftKey = event.shiftKey;

            //unselect a selected row if metakey is on
            if(selected && metaKey) {
                this.unselectRow(row, silent);
            }
            else {
                //unselect previous selection if this is single selection or multiple one with no keys
                if(this.isSingleSelection() || (this.isMultipleSelection() && event && !metaKey && !shiftKey && this.cfg.rowSelectMode === 'new' )) {
                    this.unselectAllRows();
                }
                
                //range selection with shift key
                if(this.isMultipleSelection() && event && event.shiftKey) {                    
                    this.selectRowsInRange(row);
                }
                //select current row
                else {
                    this.originRowIndex = row.index();
                    this.cursorIndex = null;
                    this.selectRow(row, silent);
                }
            } 

            if(this.cfg.disabledTextSelection) {
                PrimeFaces.clearSelection();
            }
        }
    },
    
    onRowDblclick: function(event, row) {
        if(this.cfg.disabledTextSelection) {
            PrimeFaces.clearSelection();
        }
        
        //Check if rowclick triggered this event not a clickable element in row content
        if($(event.target).is('td,span:not(.ui-c)')) {
            var rowMeta = this.getRowMeta(row);

            this.fireRowSelectEvent(rowMeta.key, 'rowDblselect');
        }
    },
    
    onRowRightClick: function(event, rowElement, cmSelMode) {
        var row = $(rowElement),
        rowMeta = this.getRowMeta(row),
        selected = row.hasClass('ui-state-highlight');

        if(cmSelMode === 'single' || !selected) {
            this.unselectAllRows();
        }

        this.selectRow(row, true);

        this.fireRowSelectEvent(rowMeta.key, 'contextMenu');

        if(this.cfg.disabledTextSelection) {
            PrimeFaces.clearSelection();
        }
    },
        
    /**
     * @param r {Row Index || Row Element}
     */
    findRow: function(r) {
        var row = r;

        if(PrimeFaces.isNumber(r)) {
            row = this.tbody.children('tr:eq(' + r + ')');
        }

        return row;
    },
    
    selectRowsInRange: function(row) {
        var rows = this.tbody.children(),
        rowMeta = this.getRowMeta(row),
        $this = this;
       
        //unselect previously selected rows with shift
        if(this.cursorIndex !== null) {
            var oldCursorIndex = this.cursorIndex,
            rowsToUnselect = oldCursorIndex > this.originRowIndex ? rows.slice(this.originRowIndex, oldCursorIndex + 1) : rows.slice(oldCursorIndex, this.originRowIndex + 1);

            rowsToUnselect.each(function(i, item) {
                $this.unselectRow($(item), true);
            });
        }

        //select rows between cursor and origin
        this.cursorIndex = row.index();

        var rowsToSelect = this.cursorIndex > this.originRowIndex ? rows.slice(this.originRowIndex, this.cursorIndex + 1) : rows.slice(this.cursorIndex, this.originRowIndex + 1);

        rowsToSelect.each(function(i, item) {
            $this.selectRow($(item), true);
        });
        
        this.fireRowSelectEvent(rowMeta.key, 'rowSelect');
    },
    
    selectRow: function(r, silent) {
        var row = this.findRow(r),
        rowMeta = this.getRowMeta(row);

        this.highlightRow(row);

        if(this.isCheckboxSelectionEnabled()) {
            if(this.cfg.nativeElements)
                row.children('td.ui-selection-column').find(':checkbox').prop('checked', true);
            else
                this.selectCheckbox(row.children('td.ui-selection-column').find('> div.ui-chkbox > div.ui-chkbox-box'));
            
            this.updateHeaderCheckbox();
        }
        
        this.addSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowSelectEvent(rowMeta.key, 'rowSelect');
        }
    },
    
    unselectRow: function(r, silent) {
        var row = this.findRow(r),
        rowMeta = this.getRowMeta(row);

        this.unhighlightRow(row);
        
        if(this.isCheckboxSelectionEnabled()) {
            if(this.cfg.nativeElements)
                row.children('td.ui-selection-column').find(':checkbox').prop('checked', false);
            else
                this.unselectCheckbox(row.children('td.ui-selection-column').find('> div.ui-chkbox > div.ui-chkbox-box'));
            
            this.updateHeaderCheckbox();
        }

        this.removeSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowUnselectEvent(rowMeta.key, "rowUnselect");
        }
    },
    
    /*
     * Highlights row as selected
     */
    highlightRow: function(row) {
        row.removeClass('ui-state-hover').addClass('ui-state-highlight').attr('aria-selected', true);
    },
    
    /*
     * Clears selected visuals
     */
    unhighlightRow: function(row) {
        row.removeClass('ui-state-highlight').attr('aria-selected', false);
    },
    
    /**
     * Sends a rowSelectEvent on server side to invoke a rowSelectListener if defined
     */
    fireRowSelectEvent: function(rowKey, behaviorEvent) {
        if(this.cfg.behaviors) {
            var selectBehavior = this.cfg.behaviors[behaviorEvent];

            if(selectBehavior) {
                var ext = {
                        params: [{name: this.id + '_instantSelectedRowKey', value: rowKey}
                    ]
                };

                selectBehavior.call(this, ext);
            }
        }
    },
    
    /**
     * Sends a rowUnselectEvent on server side to invoke a rowUnselectListener if defined
     */
    fireRowUnselectEvent: function(rowKey, behaviorEvent) {
        if(this.cfg.behaviors) {
            var unselectBehavior = this.cfg.behaviors[behaviorEvent];

            if(unselectBehavior) {
                var ext = {
                    params: [
                    {
                        name: this.id + '_instantUnselectedRowKey', 
                        value: rowKey
                    }
                    ]
                };

                unselectBehavior.call(this, ext);
            }
        }
    },
    
    /**
     * Selects the corresping row of a radio based column selection
     */
    selectRowWithRadio: function(radio) {
        var row = radio.closest('tr'),
        rowMeta = this.getRowMeta(row);

        //clean selection       
        this.unselectAllRows();
               
        //select current
        if(!this.cfg.nativeElements) {
            this.selectRadio(radio);
        }
        
        this.highlightRow(row);
        this.addSelection(rowMeta.key);
        this.writeSelections();
        this.fireRowSelectEvent(rowMeta.key, 'rowSelectRadio');
    },
    
    /**
     * Selects the corresponding row of a checkbox based column selection
     */
    selectRowWithCheckbox: function(checkbox, silent) {
        var row = checkbox.closest('tr'),
        rowMeta = this.getRowMeta(row);

        this.highlightRow(row);
        
        if(!this.cfg.nativeElements) {
            this.selectCheckbox(checkbox);
        }

        this.addSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.updateHeaderCheckbox();
            this.fireRowSelectEvent(rowMeta.key, "rowSelectCheckbox");
        }
    },
    
    /**
     * Unselects the corresponding row of a checkbox based column selection
     */
    unselectRowWithCheckbox: function(checkbox, silent) {
        var row = checkbox.closest('tr'),
        rowMeta = this.getRowMeta(row);

        this.unhighlightRow(row);
        
        if(!this.cfg.nativeElements) {
            this.unselectCheckbox(checkbox);
        }

        this.removeSelection(rowMeta.key);
        
        this.uncheckHeaderCheckbox();

        this.writeSelections();

        if(!silent) {
            this.fireRowUnselectEvent(rowMeta.key, "rowUnselectCheckbox");
        }
    },
    
    unselectAllRows: function() {
        var selectedRows = this.tbody.children('tr.ui-state-highlight'),
        checkboxSelectionEnabled = this.isCheckboxSelectionEnabled(),
        radioSelectionEnabled = this.isRadioSelectionEnabled();

        for(var i = 0; i < selectedRows.length; i++) {
            var row = selectedRows.eq(i);
            
            this.unhighlightRow(row);
            
            if(checkboxSelectionEnabled) {
                if(this.cfg.nativeElements)
                    row.children('td.ui-selection-column').find(':checkbox').prop('checked', false);
                else
                    this.unselectCheckbox(row.children('td.ui-selection-column').find('> div.ui-chkbox > div.ui-chkbox-box'));
            }
            else if(radioSelectionEnabled) {
                if(this.cfg.nativeElements)
                    row.children('td.ui-selection-column').find(':radio').prop('checked', false);
                else
                    this.unselectRadio(row.children('td.ui-selection-column').find('> div.ui-radiobutton > div.ui-radiobutton-box'));
            }
        }
        
        if(checkboxSelectionEnabled) {
            this.uncheckHeaderCheckbox();
        }
        
        this.selection = [];
        this.writeSelections();
    },
            
    selectAllRowsOnPage: function() {
        var rows = this.tbody.children('tr');
        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i);
            this.selectRow(row, true);
        }
    },
            
    unselectAllRowsOnPage: function() {
        var rows = this.tbody.children('tr');
        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i);
            this.unselectRow(row, true);
        }
    },
            
    selectAllRows: function() {
        this.selectAllRowsOnPage();
        this.selection = new Array('@all');
        this.writeSelections();
    },
    
    /**
     * Toggles all rows with checkbox
     */
    toggleCheckAll: function() {
        if(this.cfg.nativeElements) {
            var checkboxes = this.tbody.find('> tr.ui-datatable-selectable > td.ui-selection-column > :checkbox'),
            checked = this.checkAllToggler.prop('checked'),
            $this = this;
    
            checkboxes.each(function() {
                if(checked) {
                    var checkbox = $(this);
                    checkbox.prop('checked', true);
                    $this.selectRowWithCheckbox(checkbox, true);
                }
                else {
                    var checkbox = $(this);
                    checkbox.prop('checked', false);
                    $this.unselectRowWithCheckbox(checkbox, true);
                }
            });
        }
        else {
            var checkboxes = this.tbody.find('> tr.ui-datatable-selectable > td.ui-selection-column .ui-chkbox-box'),
            checked = this.checkAllToggler.hasClass('ui-state-active'),
            $this = this;

            if(checked) {
                this.checkAllToggler.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');

                checkboxes.each(function() {
                    $this.unselectRowWithCheckbox($(this), true);
                });
            } 
            else {
                this.checkAllToggler.addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');

                checkboxes.each(function() {
                    $this.selectRowWithCheckbox($(this), true);
                });
            }
        }
                
        //save state
        this.writeSelections();

        //fire toggleSelect event
        if(this.cfg.behaviors) {
            var toggleSelectBehavior = this.cfg.behaviors['toggleSelect'];

            if(toggleSelectBehavior) {
                var ext = {
                        params: [{name: this.id + '_checked', value: !checked}
                    ]
                };
                
                toggleSelectBehavior.call(this, ext);
            }
        }
    },
    
    selectCheckbox: function(checkbox) {
        if(!checkbox.hasClass('ui-state-focus')) {
            checkbox.addClass('ui-state-active');
        }
        checkbox.children('span.ui-chkbox-icon:first').removeClass('ui-icon-blank').addClass(' ui-icon-check');
        checkbox.prev().children('input').prop('checked', true);
    },
    
    unselectCheckbox: function(checkbox) {
        checkbox.removeClass('ui-state-active');
        checkbox.children('span.ui-chkbox-icon:first').addClass('ui-icon-blank').removeClass('ui-icon-check');
        checkbox.prev().children('input').prop('checked', false); 
    },
    
    selectRadio: function(radio){
        radio.removeClass('ui-state-hover');
        if(!radio.hasClass('ui-state-focus')) {
            radio.addClass('ui-state-active');
        }
        radio.children('.ui-radiobutton-icon').addClass('ui-icon-bullet').removeClass('ui-icon-blank');
        radio.prev().children('input').prop('checked', true);
    },
            
    unselectRadio: function(radio){
        radio.removeClass('ui-state-active').children('.ui-radiobutton-icon').addClass('ui-icon-blank').removeClass('ui-icon-bullet');
        radio.prev().children('input').prop('checked', false); 
    },
    
    /**
     * Expands a row to display detail content
     */
    toggleExpansion: function(toggler) {
        var row = toggler.closest('tr'),
        rowIndex = this.getRowMeta(row).index,
        iconOnly = toggler.hasClass('ui-icon'),
        labels = toggler.children('span'),
        expanded = iconOnly ? toggler.hasClass('ui-icon-circle-triangle-s'): toggler.children('span').eq(0).hasClass('ui-helper-hidden'),
        $this = this;

        //Run toggle expansion if row is not being toggled already to prevent conflicts
        if($.inArray(rowIndex, this.expansionProcess) === -1) {
            this.expansionProcess.push(rowIndex);
            
            if(expanded) {
                if(iconOnly) {
                    toggler.addClass('ui-icon-circle-triangle-e').removeClass('ui-icon-circle-triangle-s');
                }
                else {
                    labels.eq(0).removeClass('ui-helper-hidden');
                    labels.eq(1).addClass('ui-helper-hidden');
                }
                
                this.collapseRow(row);
                $this.expansionProcess = $.grep($this.expansionProcess, function(r) {
                    return (r !== rowIndex);
                });
                this.fireRowCollapseEvent(row);
            }
            else {
                if(this.cfg.rowExpandMode === 'single') {
                    this.collapseAllRows();
                }
                
                if(iconOnly) {
                    toggler.addClass('ui-icon-circle-triangle-s').removeClass('ui-icon-circle-triangle-e');
                }
                else {
                    labels.eq(0).addClass('ui-helper-hidden');
                    labels.eq(1).removeClass('ui-helper-hidden');
                }

                this.loadExpandedRowContent(row);
            }
        }
    },
    
    loadExpandedRowContent: function(row) {
        var $this = this,
        rowIndex = this.getRowMeta(row).index,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId,
            params: [{name: this.id + '_rowExpansion', value: true},
                     {name: this.id + '_expandedRowIndex', value: rowIndex},
                     {name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_skipChildren', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            if(content && $.trim(content).length) {
                                row.addClass('ui-expanded-row');
                                this.displayExpandedRow(row, content);
                            }
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.expansionProcess = $.grep($this.expansionProcess, function(r) {
                    return r !== rowIndex;
                });
            }
        };

        if(this.hasBehavior('rowToggle')) {
            var rowToggleBehavior = this.cfg.behaviors['rowToggle'];

            rowToggleBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.AjaxRequest(options); 
        }
    },
    
    displayExpandedRow: function(row, content) {
        row.after(content);
    },
    
    fireRowCollapseEvent: function(row) {
        var rowIndex = this.getRowMeta(row).index;
        
        if(this.hasBehavior('rowToggle')) {
            var ext = {
                params: [
                {
                    name: this.id + '_collapsedRowIndex', 
                    value: rowIndex
                }
                ]
            };
        
            var rowToggleBehavior = this.cfg.behaviors['rowToggle'];

            rowToggleBehavior.call(this, ext);
        } 
    },
    
    collapseRow: function(row) {
        row.removeClass('ui-expanded-row').next('.ui-expanded-row-content').remove();
    },
    
    collapseAllRows: function() {
        var $this = this;
        
        this.getExpandedRows().each(function() {
            var expandedRow = $(this);
            $this.collapseRow(expandedRow);

            var columns = expandedRow.children('td');
            for(var i = 0; i < columns.length; i++) {
                var column = columns.eq(i),
                toggler = column.children('.ui-row-toggler');

                if(toggler.length > 0) {
                    if(toggler.hasClass('ui-icon')) {
                        toggler.addClass('ui-icon-circle-triangle-e').removeClass('ui-icon-circle-triangle-s');
                    }
                    else {
                        var labels = toggler.children('span');
                        labels.eq(0).removeClass('ui-helper-hidden');
                        labels.eq(1).addClass('ui-helper-hidden');
                    }
                    break;
                }
            }
        });
    },
    
    getExpandedRows: function() {
        return this.tbody.children('.ui-expanded-row');
    },
        
    /**
     * Binds editor events non-obstrusively
     */
    bindEditEvents: function() {
        var $this = this;
        this.cfg.cellSeparator = this.cfg.cellSeparator||' ';
        
        if(this.cfg.editMode === 'row') {
            var rowEditorSelector = '> tr > td > div.ui-row-editor';
            
            this.tbody.off('click.datatable', rowEditorSelector)
                        .on('click.datatable', rowEditorSelector, null, function(e) {
                            var element = $(e.target),
                            row = element.closest('tr');
                            
                            if(element.hasClass('ui-icon-pencil')) {
                                $this.switchToRowEdit(row);
                                element.hide().siblings().show();
                            }
                            else if(element.hasClass('ui-icon-check')) {
                                $this.saveRowEdit(row);
                            }
                            else if(element.hasClass('ui-icon-close')) {
                                $this.cancelRowEdit(row);
                            }
                        });
        }
        else if(this.cfg.editMode === 'cell') {
            var cellSelector = '> tr > td.ui-editable-column';
            
            this.tbody.off('click.datatable-cell', cellSelector)
                        .on('click.datatable-cell', cellSelector, null, function(e) {
                            $this.incellClick = true;
                            
                            var cell = $(this);
                            if(!cell.hasClass('ui-cell-editing')) {
                                $this.showCellEditor($(this));
                            }
                        });
                        
            $(document).off('click.datatable-cell-blur' + this.id)
                        .on('click.datatable-cell-blur' + this.id, function(e) {                            
                            if(!$this.incellClick && $this.currentCell && !$this.contextMenuClick) {
                                $this.saveCell($this.currentCell);
                            }
                            
                            $this.incellClick = false;
                            $this.contextMenuClick = false;
                        });
        }
    },
    
    switchToRowEdit: function(row) {
        this.showRowEditors(row);
        
        if(this.hasBehavior('rowEditInit')) {
            var rowEditInitBehavior = this.cfg.behaviors['rowEditInit'],
            rowIndex = this.getRowMeta(row).index;
            
            var ext = {
                params: [{name: this.id + '_rowEditIndex', value: rowIndex}]
            };

            rowEditInitBehavior.call(this, ext);
        }
    },
    
    showRowEditors: function(row) {
        row.addClass('ui-state-highlight ui-row-editing').children('td.ui-editable-column').each(function() {
            var column = $(this);

            column.find('.ui-cell-editor-output').hide();
            column.find('.ui-cell-editor-input').show();
        });
    },
    
    showCellEditor: function(c) {
        this.incellClick = true;
        
        var cell = null,
        $this = this;
                    
        if(c) {
            cell = c;
                        
            //remove contextmenu selection highlight
            if(this.contextMenuCell) {
                this.contextMenuCell.parent().removeClass('ui-state-highlight');
            }
        }
        else {
            cell = this.contextMenuCell;
        }
        
        if(this.currentCell) {
            $this.saveCell(this.currentCell);
        }
        
        this.currentCell = cell;
                
        var cellEditor = cell.children('div.ui-cell-editor'),
        displayContainer = cellEditor.children('div.ui-cell-editor-output'),
        inputContainer = cellEditor.children('div.ui-cell-editor-input'),
        inputs = inputContainer.find(':input:enabled'),
        multi = inputs.length > 1;
                                        
        cell.addClass('ui-state-highlight ui-cell-editing');
        displayContainer.hide();
        inputContainer.show();
        inputs.eq(0).focus().select();
        
        //metadata
        if(multi) {
            var oldValues = [];
            for(var i = 0; i < inputs.length; i++) {
                oldValues.push(inputs.eq(i).val());
            }
            
            cell.data('multi-edit', true);
            cell.data('old-value', oldValues);
        } 
        else {
            cell.data('multi-edit', false);
            cell.data('old-value', inputs.eq(0).val());
        }
        
        //bind events on demand
        if(!cell.data('edit-events-bound')) {
            cell.data('edit-events-bound', true);
            
            inputs.on('keydown.datatable-cell', function(e) {
                    var keyCode = $.ui.keyCode,
                    shiftKey = e.shiftKey,
                    key = e.which,
                    input = $(this);

                    if(key === keyCode.ENTER || key == keyCode.NUMPAD_ENTER) {
                        $this.saveCell(cell);

                        e.preventDefault();
                    }
                    else if(key === keyCode.TAB) {
                        if(multi) {
                            var focusIndex = shiftKey ? input.index() - 1 : input.index() + 1;

                            if(focusIndex < 0 || (focusIndex === inputs.length)) {
                                $this.tabCell(cell, !shiftKey);                                
                            } else {
                                inputs.eq(focusIndex).focus();
                            }
                        }
                        else {
                            $this.tabCell(cell, !shiftKey);
                        }
                        
                        e.preventDefault();
                    }
                })
                .on('focus.datatable-cell click.datatable-cell', function(e) {
                    $this.currentCell = cell;
                });
        }        
    },
    
    tabCell: function(cell, forward) {
        var targetCell = forward ? cell.next() : cell.prev();
        if(targetCell.length == 0) {
            var tabRow = forward ? cell.parent().next() : cell.parent().prev();
            targetCell = forward ? tabRow.children('td.ui-editable-column:first') : tabRow.children('td.ui-editable-column:last');
        }

        this.showCellEditor(targetCell);
    },
    
    saveCell: function(cell) {
        var inputs = cell.find('div.ui-cell-editor-input :input:enabled'),
        changed = false,
        $this = this;
        
        if(cell.data('multi-edit')) {
            var oldValues = cell.data('old-value');
            for(var i = 0; i < inputs.length; i++) {
                if(inputs.eq(i).val() != oldValues[i]) {
                    changed = true;
                    break;
                }
            }
        } 
        else {
            changed = (inputs.eq(0).val() != cell.data('old-value'));
        }
        
        if(changed)
            $this.doCellEditRequest(cell);
        else
            $this.viewMode(cell);
        
        this.currentCell = null;
    },
        
    viewMode: function(cell) {
        var cellEditor = cell.children('div.ui-cell-editor'),
        editableContainer = cellEditor.children('div.ui-cell-editor-input'),
        displayContainer = cellEditor.children('div.ui-cell-editor-output');
        
        cell.removeClass('ui-cell-editing ui-state-error ui-state-highlight');
        displayContainer.show();
        editableContainer.hide();
        cell.removeData('old-value').removeData('multi-edit');
    },
    
    doCellEditRequest: function(cell) {
        var rowMeta = this.getRowMeta(cell.closest('tr')),
        cellEditor = cell.children('.ui-cell-editor'),
        cellEditorId = cellEditor.attr('id'),
        cellIndex = cell.index(),
        cellInfo = rowMeta.index + ',' + cellIndex,
        $this = this;

        if(rowMeta.key) {
            cellInfo = cellInfo + ',' + rowMeta.key;
        }

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [{name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_cellInfo', value: cellInfo},
                     {name: cellEditorId, value: cellEditorId}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            cellEditor.children('.ui-cell-editor-output').html(content);
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args) {                            
                if(args.validationFailed)
                    cell.addClass('ui-state-error');
                else
                    $this.viewMode(cell);
            }
        };

        if(this.hasBehavior('cellEdit')) {
            this.cfg.behaviors['cellEdit'].call(this, options);
        } 
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
    
    /**
     * Saves the edited row
     */
    saveRowEdit: function(rowEditor) {
        this.doRowEditRequest(rowEditor, 'save');
    },
    
    /**
     * Cancels row editing
     */
    cancelRowEdit: function(rowEditor) {
        this.doRowEditRequest(rowEditor, 'cancel');
    },
    
    /**
     * Sends an ajax request to handle row save or cancel
     */
    doRowEditRequest: function(rowEditor, action) {
        var row = rowEditor.closest('tr'),
        rowIndex = this.getRowMeta(row).index,
        expanded = row.hasClass('ui-expanded-row'),
        $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId,
            params: [{name: this.id + '_rowEditIndex', value: this.getRowMeta(row).index},
                     {name: this.id + '_rowEditAction', value: action},
                     {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            if(expanded) {
                                this.collapseRow(row);
                            }

                            this.updateRow(row, content);
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args) {
                if(args && args.validationFailed) {
                    $this.invalidateRow(rowIndex);
                }
            }
        };
        
        if(action === 'save') {
            this.getRowEditors(row).each(function() {
                options.params.push({name: this.id, value: this.id});
            });
        }

        if(action === 'save' && this.hasBehavior('rowEdit')) {
            this.cfg.behaviors['rowEdit'].call(this, options);
        }
        else if(action === 'cancel' && this.hasBehavior('rowEditCancel')) {
            this.cfg.behaviors['rowEditCancel'].call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options); 
        }
    },
    
    /*
     * Updates row with given content
     */
    updateRow: function(row, content) {
        row.replaceWith(content);
    },
    
    /**
     * Displays row editors in invalid format
     */
    invalidateRow: function(index) {
        var i = (this.paginator) ? (index % this.paginator.getRows()) : index;
        this.tbody.children('tr').eq(i).addClass('ui-widget-content ui-row-editing ui-state-error');
    },
    
    /**
     * Finds all editors of a row
     */
    getRowEditors: function(row) {
        return row.find('div.ui-cell-editor');
    },

    /**
     * Returns the paginator instance if any defined
     */
    getPaginator: function() {
        return this.paginator;
    },
    
    /**
     * Writes selected row ids to state holder
     */
    writeSelections: function() {
        $(this.selectionHolder).val(this.selection.join(','));
    },
    
    isSingleSelection: function() {
        return this.cfg.selectionMode == 'single';
    },
    
    isMultipleSelection: function() {
        return this.cfg.selectionMode == 'multiple' || this.isCheckboxSelectionEnabled();
    },
    
    /**
     * Clears the selection state
     */
    clearSelection: function() {
        this.selection = [];

        $(this.selectionHolder).val('');
    },
    
    /**
     * Returns true|false if selection is enabled|disabled
     */
    isSelectionEnabled: function() {
        return this.cfg.selectionMode != undefined || this.cfg.columnSelectionMode != undefined;
    },

    /**
     * Returns true|false if checkbox selection is enabled|disabled
     */
    isCheckboxSelectionEnabled: function() {
        return this.cfg.selectionMode === 'checkbox';
    },
    
    /**
     * Returns true|false if radio selection is enabled|disabled
     */
    isRadioSelectionEnabled: function() {
        return this.cfg.selectionMode === 'radio';
    },
            
    /**
     * Clears table filters
     */
    clearFilters: function() {
        this.thead.find('> tr > th.ui-filter-column > .ui-column-filter').val('');
        $(this.jqId + '\\:globalFilter').val('');

        this.filter();
    },
    
    setupResizableColumns: function() {
        this.cfg.resizeMode = this.cfg.resizeMode||'fit';
        this.hasColumnGroup = this.hasColGroup();
        if(this.hasColumnGroup) {
            this.addGhostRow();
        }
        
        this.fixColumnWidths();
        
        if(!this.cfg.liveResize) {
            this.resizerHelper = $('<div class="ui-column-resizer-helper ui-state-highlight"></div>').appendTo(this.jq);
        }
        
        this.addResizers();
        
        var resizers = this.thead.find('> tr > th > span.ui-column-resizer'),
        $this = this;
            
        resizers.draggable({
            axis: 'x',
            start: function(event, ui) {
                ui.helper.data('originalposition', ui.helper.offset());
                
                if($this.cfg.liveResize) {
                    $this.jq.css('cursor', 'col-resize');
                }
                else {
                    var height = $this.cfg.scrollable ? $this.scrollBody.height() : $this.thead.parent().height() - $this.thead.height() - 1;
                    $this.resizerHelper.height(height);
                    $this.resizerHelper.show();
                }
            },
            drag: function(event, ui) {
                if($this.cfg.liveResize) {
                    $this.resize(event, ui);
                }
                else {
                    $this.resizerHelper.offset({
                        left: ui.helper.offset().left + ui.helper.width() / 2, 
                        top: $this.thead.offset().top + $this.thead.height()
                    });  
                }                
            },
            stop: function(event, ui) {
                ui.helper.css({
                    'left': '',
                    'top': '0px'
                });
                
                if($this.cfg.liveResize) {
                    $this.jq.css('cursor', 'default');
                } else {
                    $this.resize(event, ui);
                    $this.resizerHelper.hide();
                }
                
                if($this.cfg.resizeMode === 'expand') {
                    setTimeout(function() {
                        $this.fireColumnResizeEvent(ui.helper.parent());
                    }, 5);
                }
                else {
                    $this.fireColumnResizeEvent(ui.helper.parent());
                }
                
                if($this.cfg.stickyHeader) {
                    $this.thead.find('.ui-column-filter').prop('disabled', false);
                    $this.clone = $this.thead.clone(true);
                    $this.cloneContainer.find('thead').remove();
                    $this.cloneContainer.children('table').append($this.clone);
                    $this.thead.find('.ui-column-filter').prop('disabled', true);
                }
            },
            containment: this.jq
        });
    },
    
    fireColumnResizeEvent: function(columnHeader) {
        if(this.hasBehavior('colResize')) {
            var options = {
                source: this.id,
                process: this.id,
                params: [
                    {name: this.id + '_colResize', value: true},
                    {name: this.id + '_columnId', value: columnHeader.attr('id')},
                    {name: this.id + '_width', value: columnHeader.width()},
                    {name: this.id + '_height', value: columnHeader.height()}
                ]
            };
                
            this.cfg.behaviors['colResize'].call(this, options);
        }
    },
    
    hasColGroup: function() {
        return this.thead.children('tr').length > 1;
    },
    
    addGhostRow: function() {
        var dataColumnsCount = this.tbody.find('tr:first').children('td').length,
        columnMarkup = '';

        for(var i = 0; i < dataColumnsCount; i++) {
            columnMarkup += '<th style="height:0px;border-bottom-width: 0px;border-top-width: 0px;padding-top: 0px;padding-bottom: 0px;outline: 0 none;" class="ui-resizable-column"></th>';
        }
        
        this.thead.prepend('<tr>' + columnMarkup + '</tr>');

        if(this.cfg.scrollable) {         
            this.theadClone.prepend('<tr>' + columnMarkup + '</tr>');
            this.footerTable.children('tfoot').prepend('<tr>' + columnMarkup + '</tr>');
        }
    },
    
    findGroupResizer: function(ui) {
        for(var i = 0; i < this.groupResizers.length; i++) {
            var groupResizer = this.groupResizers.eq(i);
            if(groupResizer.offset().left === ui.helper.data('originalposition').left) {
                return groupResizer;
            }
        }
        
        return null;
    },
    
    addResizers: function() {
        var resizableColumns = this.thead.find('> tr > th.ui-resizable-column');
        resizableColumns.prepend('<span class="ui-column-resizer">&nbsp;</span>');
        
        if(this.cfg.resizeMode === 'fit') {
            resizableColumns.filter(':last-child').children('span.ui-column-resizer').hide();
        }
        
        if(this.hasColumnGroup) {
            this.groupResizers = this.thead.find('> tr:first > th > .ui-column-resizer');
        }
    },
    
    resize: function(event, ui) {
        var columnHeader, nextColumnHeader, change = null, newWidth = null, nextColumnWidth = null, 
        expandMode = (this.cfg.resizeMode === 'expand'),
        table = this.thead.parent();
                
        if(this.hasColumnGroup) {
            var groupResizer = this.findGroupResizer(ui);
            if(!groupResizer) {
                return;
            }
            
            columnHeader = groupResizer.parent();
        }
        else {
            columnHeader = ui.helper.parent();
        }
        
        var nextColumnHeader = columnHeader.next();
        
        if(this.cfg.liveResize) {
            change = columnHeader.outerWidth() - (event.pageX - columnHeader.offset().left),
            newWidth = (columnHeader.width() - change),
            nextColumnWidth = (nextColumnHeader.width() + change);
        }
        else {
            change = (ui.position.left - ui.originalPosition.left),
            newWidth = (columnHeader.width() + change),
            nextColumnWidth = (nextColumnHeader.width() - change);
        }
                        
        if((newWidth > 15 && nextColumnWidth > 15) || (expandMode && newWidth > 15)) {          
            if(expandMode) {
                table.width(table.width() + change);
                setTimeout(function() {
                    columnHeader.width(newWidth);
                }, 1);
            }
            else {
                columnHeader.width(newWidth);
                nextColumnHeader.width(nextColumnWidth);
            }
            
            if(this.cfg.scrollable) {
                var cloneTable = this.theadClone.parent(),
                colIndex = columnHeader.index();
        
                if(expandMode) {
                    var $this = this;
                    
                    //body
                    cloneTable.width(cloneTable.width() + change);
                    
                    //footer
                    this.footerTable.width(this.footerTable.width() + change);
                    
                    setTimeout(function() {
                        if($this.hasColumnGroup) {
                            $this.theadClone.find('> tr:first').children('th').eq(colIndex).width(newWidth);            //body
                            $this.footerTable.find('> tfoot > tr:first').children('th').eq(colIndex).width(newWidth);   //footer
                        }
                        else {
                            $this.theadClone.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth);   //body
                            $this.footerCols.eq(colIndex).width(newWidth);                                                          //footer
                        }
                    }, 1);
                }
                else {
                    if(this.hasColumnGroup) {
                        //body
                        this.theadClone.find('> tr:first').children('th').eq(colIndex).width(newWidth);
                        this.theadClone.find('> tr:first').children('th').eq(colIndex + 1).width(nextColumnWidth);
                        
                        //footer
                        this.footerTable.find('> tfoot > tr:first').children('th').eq(colIndex).width(newWidth);
                        this.footerTable.find('> tfoot > tr:first').children('th').eq(colIndex + 1).width(nextColumnWidth);
                    }
                    else {
                        //body
                        this.theadClone.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth);
                        this.theadClone.find(PrimeFaces.escapeClientId(nextColumnHeader.attr('id') + '_clone')).width(nextColumnWidth);
                        
                        //footer
                        if(this.footerCols.length > 0) {
                            var footerCol = this.footerCols.eq(colIndex),
                            nextFooterCol = footerCol.next();

                            footerCol.width(newWidth);
                            nextFooterCol.width(nextColumnWidth);
                        }
                    }
                }
            }            
        }
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }
    
        return false;
    },
    
    /**
     * Remove given rowIndex from selection
     */
    removeSelection: function(rowIndex) {        
        this.selection = $.grep(this.selection, function(value) {
            return value != rowIndex;
        });
    },
    
    /**
     * Adds given rowIndex to selection if it doesn't exist already
     */
    addSelection: function(rowIndex) {
        if(!this.isSelected(rowIndex)) {
            this.selection.push(rowIndex);
        }
    },
    
    /**
     * Finds if given rowIndex is in selection
     */
    isSelected: function(rowIndex) {
        return PrimeFaces.inArray(this.selection, rowIndex);
    },
    
    getRowMeta: function(row) {
        var meta = {
            index: row.data('ri'),
            key:  row.attr('data-rk')
        };

        return meta;
    },
    
    setupDraggableColumns: function() {
        this.orderStateHolder = $(this.jqId + '_columnOrder');
        this.saveColumnOrder();
        
        this.dragIndicatorTop = $('<span class="ui-icon ui-icon-arrowthick-1-s" style="position:absolute"/></span>').hide().appendTo(this.jq);
        this.dragIndicatorBottom = $('<span class="ui-icon ui-icon-arrowthick-1-n" style="position:absolute"/></span>').hide().appendTo(this.jq);

        var $this = this;

        $(this.jqId + ' thead th').draggable({
            appendTo: 'body',
            opacity: 0.75,
            cursor: 'move',
            scope: this.id,
            cancel: ':input,.ui-column-resizer',
            drag: function(event, ui) {
                var droppable = ui.helper.data('droppable-column');

                if(droppable) {
                    var droppableOffset = droppable.offset(),
                    topArrowY = droppableOffset.top - 10,
                    bottomArrowY = droppableOffset.top + droppable.height() + 8,
                    arrowX = null;
                    
                    //calculate coordinates of arrow depending on mouse location
                    if(event.originalEvent.pageX >= droppableOffset.left + (droppable.width() / 2)) {
                        var nextDroppable = droppable.next();
                        if(nextDroppable.length == 1)
                            arrowX = nextDroppable.offset().left - 9;
                        else
                            arrowX = droppable.offset().left + droppable.innerWidth() - 9;
                            
                        ui.helper.data('drop-location', 1);     //right
                    }
                    else {
                        arrowX = droppableOffset.left  - 9;
                        ui.helper.data('drop-location', -1);    //left
                    }
                    
                    $this.dragIndicatorTop.offset({
                        'left': arrowX, 
                        'top': topArrowY - 3
                    }).show();
                    
                    $this.dragIndicatorBottom.offset({
                        'left': arrowX, 
                        'top': bottomArrowY - 3
                    }).show();
                }
            },
            stop: function(event, ui) {
                //hide dnd arrows
                $this.dragIndicatorTop.css({
                    'left':0, 
                    'top':0
                }).hide();
                
                $this.dragIndicatorBottom.css({
                    'left':0, 
                    'top':0
                }).hide();
            },
            helper: function() {
                var header = $(this),
                helper = $('<div class="ui-widget ui-state-default" style="padding:4px 10px;text-align:center;"></div>');

                helper.width(header.width());
                helper.height(header.height());

                helper.html(header.html());

                return helper.get(0);
            }
        }).droppable({
            hoverClass:'ui-state-highlight',
            tolerance:'pointer',
            scope: this.id,
            over: function(event, ui) {
                ui.helper.data('droppable-column', $(this));
            },
            drop: function(event, ui) {
                var draggedColumnHeader = ui.draggable,
                dropLocation = ui.helper.data('drop-location'),
                droppedColumnHeader =  $(this),
                draggedColumnFooter = null,
                droppedColumnFooter = null;
                                        
                var draggedCells = $this.tbody.find('> tr:not(.ui-expanded-row-content) > td:nth-child(' + (draggedColumnHeader.index() + 1) + ')'),
                droppedCells = $this.tbody.find('> tr:not(.ui-expanded-row-content) > td:nth-child(' + (droppedColumnHeader.index() + 1) + ')');
                
                if($this.tfoot.length) {
                    var footerColumns = $this.tfoot.find('> tr > td'),
                    draggedColumnFooter = footerColumns.eq(draggedColumnHeader.index()),
                    droppedColumnFooter = footerColumns.eq(droppedColumnHeader.index());
                }
                
                //drop right
                if(dropLocation > 0) {
                    if($this.cfg.resizableColumns) {
                        if(droppedColumnHeader.next().length) {
                            droppedColumnHeader.children('span.ui-column-resizer').show();
                            draggedColumnHeader.children('span.ui-column-resizer').hide();
                        }
                    }
                                        
                    draggedColumnHeader.insertAfter(droppedColumnHeader);

                    draggedCells.each(function(i, item) {
                        $(this).insertAfter(droppedCells.eq(i));
                    });
                    
                    if(draggedColumnFooter && droppedColumnFooter) {
                        draggedColumnFooter.insertAfter(droppedColumnFooter);
                    }
                    
                    //sync clone
                    if($this.cfg.scrollable) {
                        var draggedColumnClone = $(document.getElementById(draggedColumnHeader.attr('id') + '_clone')),
                        droppedColumnClone = $(document.getElementById(droppedColumnHeader.attr('id') + '_clone'));
                        draggedColumnClone.insertAfter(droppedColumnClone);
                    }
                }
                //drop left
                else {
                    draggedColumnHeader.insertBefore(droppedColumnHeader);

                    draggedCells.each(function(i, item) {
                        $(this).insertBefore(droppedCells.eq(i));
                    });
                    
                    if(draggedColumnFooter && droppedColumnFooter) {
                        draggedColumnFooter.insertBefore(droppedColumnFooter);
                    }
                    
                    //sync clone
                    if($this.cfg.scrollable) {
                        var draggedColumnClone = $(document.getElementById(draggedColumnHeader.attr('id') + '_clone')),
                        droppedColumnClone = $(document.getElementById(droppedColumnHeader.attr('id') + '_clone'));
                        draggedColumnClone.insertBefore(droppedColumnClone);
                    }
                }
                               
                //save order
                $this.saveColumnOrder();

                //fire colReorder event
                if($this.cfg.behaviors) {
                    var columnReorderBehavior = $this.cfg.behaviors['colReorder'];

                    if(columnReorderBehavior) {            
                        columnReorderBehavior.call($this);
                    }
                }
            }
        });
    },
    
    saveColumnOrder: function() {
        var columnIds = [],
        columns = $(this.jqId + ' thead:first th');

        columns.each(function(i, item) {
            columnIds.push($(item).attr('id'));
        });

        this.orderStateHolder.val(columnIds.join(','));
    },
    
    makeRowsDraggable: function() {
        var $this = this;
        
        this.tbody.sortable({
            placeholder: 'ui-datatable-rowordering ui-state-active',
            cursor: 'move',
            handle: 'td,span:not(.ui-c)',
            appendTo: document.body,
            helper: function(event, ui) {
                var cells = ui.children(),
                helper = $('<div class="ui-datatable ui-widget"><table><tbody></tbody></table></div>'),
                helperRow = ui.clone(),
                helperCells = helperRow.children();

                for(var i = 0; i < helperCells.length; i++) {
                    helperCells.eq(i).width(cells.eq(i).width());
                }
                
                helperRow.appendTo(helper.find('tbody'));

                return helper;
            },
            update: function(event, ui) {
                var fromIndex = ui.item.data('ri'),
                toIndex = $this.paginator ? $this.paginator.getFirst() + ui.item.index(): ui.item.index();

                $this.syncRowParity();
                
                var options = {
                    source: $this.id,
                    process: $this.id,
                    params: [
                        {name: $this.id + '_rowreorder', value: true},
                        {name: $this.id + '_fromIndex', value: fromIndex},
                        {name: $this.id + '_toIndex', value: toIndex},
                        {name: this.id + '_skipChildren', value: true}
                    ]
                }
                
                if($this.hasBehavior('rowReorder')) {
                    $this.cfg.behaviors['rowReorder'].call($this, options);
                } 
                else {
                    PrimeFaces.ajax.Request.handle(options);
                }
            },
            change: function(event, ui) {
                if($this.cfg.scrollable) {
                    PrimeFaces.scrollInView($this.scrollBody, ui.placeholder);
                }
            }
        });
    },
    
    syncRowParity: function() {
        var rows = this.tbody.children('tr.ui-widget-content'),
        first = this.paginator ? this.paginator.getFirst(): 0;
        
        for(var i = first; i < rows.length; i++) {
            var row = rows.eq(i);
            
            row.data('ri', i).removeClass('ui-datatable-even ui-datatable-odd');
            
            if(i % 2 === 0)
                row.addClass('ui-datatable-even');
            else
                row.addClass('ui-datatable-odd');
                
        }
    },
    
    /**
     * Returns if there is any data displayed
     */
    isEmpty: function() {
        return this.tbody.children('tr.ui-datatable-empty-message').length === 1;
    },
    
    getSelectedRowsCount: function() {
        return this.isSelectionEnabled() ? this.selection.length : 0;
    },
    
    updateHeaderCheckbox: function() {
        if(this.isEmpty()) {
            this.uncheckHeaderCheckbox();
            this.disableHeaderCheckbox();
        }
        else {
            var checkboxes, selectedCheckboxes, enabledCheckboxes, disabledCheckboxes;
            
            if(this.cfg.nativeElements) {
                checkboxes = this.tbody.find('> tr > td.ui-selection-column > :checkbox');
                enabledCheckboxes = checkboxes.filter(':enabled');
                disabledCheckboxes = checkboxes.filter(':disabled');
                selectedCheckboxes = enabledCheckboxes.filter(':checked');
            }
            else {
                checkboxes = this.tbody.find('> tr > td.ui-selection-column .ui-chkbox-box');
                enabledCheckboxes = checkboxes.filter(':not(.ui-state-disabled)');
                disabledCheckboxes = checkboxes.filter('.ui-state-disabled');
                selectedCheckboxes = enabledCheckboxes.filter('.ui-state-active');
            }
                        
            if(enabledCheckboxes.length && enabledCheckboxes.length === selectedCheckboxes.length)
               this.checkHeaderCheckbox();
            else
               this.uncheckHeaderCheckbox();
               
            if(checkboxes.length === disabledCheckboxes.length)
               this.disableHeaderCheckbox();
            else
               this.enableHeaderCheckbox();
        }
    },
    
    checkHeaderCheckbox: function() {
        if(this.cfg.nativeElements)
            this.checkAllToggler.prop('checked', true);
        else
            this.checkAllToggler.addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
    },
    
    uncheckHeaderCheckbox: function() {
        if(this.cfg.nativeElements)
            this.checkAllToggler.prop('checked', false);
        else
            this.checkAllToggler.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
    },
    
    disableHeaderCheckbox: function() {
        if(this.cfg.nativeElements)
            this.checkAllToggler.prop('disabled', true);
        else
            this.checkAllToggler.addClass('ui-state-disabled');
    },
    
    enableHeaderCheckbox: function() {
        if(this.cfg.nativeElements)
            this.checkAllToggler.prop('disabled', false);
        else
            this.checkAllToggler.removeClass('ui-state-disabled');
    },
            
    setupStickyHeader: function() {
        var table = this.thead.parent(),
        offset = table.offset(),
        win = $(window),
        $this = this,
        stickyNS = 'scroll.' + this.id,
        resizeNS = 'resize.sticky-' + this.id; 

        this.cloneContainer = $('<div class="ui-datatable ui-datatable-sticky ui-widget"><table></table></div>');
        this.clone = this.thead.clone(true);
        this.cloneContainer.children('table').append(this.clone);
        
        this.cloneContainer.css({
            position: 'absolute',
            width: table.outerWidth(),
            top: offset.top,
            left: offset.left,
            'z-index': ++PrimeFaces.zindex
        })
        .appendTo(this.jq);

        win.off(stickyNS).on(stickyNS, function() {
            var scrollTop = win.scrollTop(),
            tableOffset = table.offset();
            
            if(scrollTop > tableOffset.top) {
                $this.cloneContainer.css({
                                        'position': 'fixed',
                                        'top': '0px'
                                    })
                                    .addClass('ui-shadow ui-sticky');
                
                if(scrollTop >= (tableOffset.top + $this.tbody.height()))
                    $this.cloneContainer.hide();
                else
                    $this.cloneContainer.show();
            }
            else {
                $this.cloneContainer.css({
                                        'position': 'absolute',
                                        'top': tableOffset.top
                                    })
                                    .removeClass('ui-shadow ui-sticky');
            }
        })
        .off(resizeNS).on(resizeNS, function() {
            $this.cloneContainer.width(table.outerWidth());
        });
        
        //filter support
        this.thead.find('.ui-column-filter').prop('disabled', true);
    }

});

/**
 * PrimeFaces DataTable with Frozen Columns Widget
 */
PrimeFaces.widget.FrozenDataTable = PrimeFaces.widget.DataTable.extend({
        
    setupScrolling: function() {
        this.scrollLayout = this.jq.find('> table > tbody > tr > td.ui-datatable-frozenlayout-right');
        this.frozenLayout = this.jq.find('> table > tbody > tr > td.ui-datatable-frozenlayout-left');
        this.scrollContainer = this.jq.find('> table > tbody > tr > td.ui-datatable-frozenlayout-right > .ui-datatable-scrollable-container');
        this.frozenContainer = this.jq.find('> table > tbody > tr > td.ui-datatable-frozenlayout-left > .ui-datatable-frozen-container');
        this.scrollHeader = this.scrollContainer.children('.ui-datatable-scrollable-header');
        this.scrollHeaderBox = this.scrollHeader.children('div.ui-datatable-scrollable-header-box');
        this.scrollBody = this.scrollContainer.children('.ui-datatable-scrollable-body');
        this.scrollFooter = this.scrollContainer.children('.ui-datatable-scrollable-footer');
        this.scrollFooterBox = this.scrollFooter.children('div.ui-datatable-scrollable-footer-box');
        this.scrollStateHolder = $(this.jqId + '_scrollState');
        this.scrollHeaderTable = this.scrollHeaderBox.children('table');
        this.scrollBodyTable = this.scrollBody.children('table');
        this.scrollThead = this.thead.eq(1);
        this.scrollTbody = this.tbody.eq(1);
        this.scrollFooterTable = this.scrollFooterBox.children('table');
        this.scrollFooterCols = this.scrollFooter.find('> .ui-datatable-scrollable-footer-box > table > tfoot > tr > td');  
        this.frozenHeader = this.frozenContainer.children('.ui-datatable-scrollable-header');
        this.frozenBody = this.frozenContainer.children('.ui-datatable-scrollable-body');
        this.frozenBodyTable = this.frozenBody.children('table');
        this.frozenThead = this.thead.eq(0);
        this.frozenTbody = this.tbody.eq(0);
        this.frozenFooter = this.frozenContainer.children('.ui-datatable-scrollable-footer');
        this.frozenFooterTable = this.frozenFooter.find('> .ui-datatable-scrollable-footer-box > table');
        this.frozenFooterCols = this.frozenFooter.find('> .ui-datatable-scrollable-footer-box > table > tfoot > tr > td');
        this.percentageScrollHeight = this.cfg.scrollHeight && (this.cfg.scrollHeight.indexOf('%') !== -1);
        this.percentageScrollWidth = this.cfg.scrollWidth && (this.cfg.scrollWidth.indexOf('%') !== -1);

        this.frozenThead.find('> tr > th').addClass('ui-frozen-column');
        
        var $this = this,
        scrollBarWidth = this.getScrollbarWidth() + 'px';

        if(this.cfg.scrollHeight) {
            if(this.percentageScrollHeight) {
                this.adjustScrollHeight();
            }
            
            if(this.hasVerticalOverflow()) {
                this.scrollHeaderBox.css('margin-right', scrollBarWidth);
                this.scrollFooterBox.css('margin-right', scrollBarWidth);
            }
        }

        this.fixColumnWidths();      

        if(this.cfg.scrollWidth) {
            if(this.percentageScrollWidth)
                this.adjustScrollWidth();
            else
                this.setScrollWidth(parseInt(this.cfg.scrollWidth));
            
            if(this.hasVerticalOverflow()) {
                if(PrimeFaces.env.browser.webkit === true)
                    this.frozenBody.append('<div style="height:' + scrollBarWidth + ';border:1px solid transparent"></div>');
                else if(PrimeFaces.isIE(8))
                    this.frozenBody.append('<div style="height:' + scrollBarWidth + '"></div>');
                else
                    this.frozenBodyTable.css('margin-bottom', scrollBarWidth);
            }
        }
        
        this.cloneHead();  
        
        this.restoreScrollState();

        if(this.cfg.liveScroll) {
            this.scrollOffset = 0;
            this.cfg.liveScrollBuffer = (100 - this.cfg.liveScrollBuffer) / 100;
            this.shouldLiveScroll = true;       
            this.loadingLiveScroll = false;
            this.allLoadedLiveScroll = $this.cfg.scrollStep >= $this.cfg.scrollLimit;            
        }

        this.scrollBody.scroll(function() {
            var scrollLeft = $this.scrollBody.scrollLeft(),
            scrollTop = $this.scrollBody.scrollTop();
            $this.scrollHeaderBox.css('margin-left', -scrollLeft);
            $this.scrollFooterBox.css('margin-left', -scrollLeft);
            $this.frozenBody.scrollTop(scrollTop);

            if($this.shouldLiveScroll) {
                var scrollTop = this.scrollTop,
                scrollHeight = this.scrollHeight,
                viewportHeight = this.clientHeight;

                if((scrollTop >= ((scrollHeight * $this.cfg.liveScrollBuffer) - (viewportHeight))) && $this.shouldLoadLiveScroll()) {
                    $this.loadLiveRows();
                }
            }

            $this.saveScrollState();
        });

        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.jq.is(':visible')) {
                if($this.percentageScrollHeight)
                    $this.adjustScrollHeight();

                if($this.percentageScrollWidth)
                    $this.adjustScrollWidth();
            }
        });
    },
    
    cloneHead: function() {
        this.frozenTheadClone = this.frozenThead.clone();
        this.frozenTheadClone.find('th').each(function() {
            var header = $(this);
            header.attr('id', header.attr('id') + '_clone');
            $(this).children().not('.ui-column-title').remove();
        });
        this.frozenTheadClone.removeAttr('id').addClass('ui-datatable-scrollable-theadclone').height(0).prependTo(this.frozenBodyTable);
        
        this.scrollTheadClone = this.scrollThead.clone();
        this.scrollTheadClone.find('th').each(function() {
            var header = $(this);
            header.attr('id', header.attr('id') + '_clone');
            $(this).children().not('.ui-column-title').remove();
        });
        this.scrollTheadClone.removeAttr('id').addClass('ui-datatable-scrollable-theadclone').height(0).prependTo(this.scrollBodyTable);
    },
    
    hasVerticalOverflow: function() {
        return this.scrollBodyTable.outerHeight() > this.scrollBody.outerHeight();
    },
            
    adjustScrollHeight: function() {
        var relativeHeight = this.jq.parent().innerHeight() * (parseInt(this.cfg.scrollHeight) / 100),
        tableHeaderHeight = this.jq.children('.ui-datatable-header').outerHeight(true),
        tableFooterHeight = this.jq.children('.ui-datatable-footer').outerHeight(true),
        scrollersHeight = (this.scrollHeader.innerHeight() + this.scrollFooter.innerHeight()),
        paginatorsHeight = this.paginator ? this.paginator.getContainerHeight(true) : 0,
        height = (relativeHeight - (scrollersHeight + paginatorsHeight + tableHeaderHeight + tableFooterHeight));
        
        this.scrollBody.height(height);
        this.frozenBody.height(height);
    },
    
    //@Override
    adjustScrollWidth: function() {
        var width = parseInt((this.scrollLayout.innerWidth() * (parseInt(this.cfg.scrollWidth) / 100)));
        this.setScrollWidth(width);
    },
    
    setScrollWidth: function(width) {
        var $this = this,
        headerWidth = width + this.frozenLayout.width();
        
        this.jq.children('.ui-widget-header').each(function() {
            $this.setOuterWidth($(this), headerWidth);
        });
        this.scrollHeader.width(width);
        this.scrollBody.css('margin-right', 0).width(width);
        this.scrollFooter.width(width);
    },
    
    fixColumnWidths: function() {        
        if(!this.columnWidthsFixed) {
            if(PrimeFaces.isIE(7)) {
                this.bodyTable.css('width', 'auto');
            }
            
            if(this.cfg.scrollable) {
                this._fixColumnWidths(this.scrollHeader, this.scrollFooterCols, this.scrollColgroup);
                this._fixColumnWidths(this.frozenHeader, this.frozenFooterCols, this.frozenColgroup);
            }
            else {
                this.jq.find('> .ui-datatable-tablewrapper > table > thead > tr > th').each(function() {
                    var col = $(this);
                    col.width(col.width());
                });
            }

            this.columnWidthsFixed = true;
        }
    },
    
    _fixColumnWidths: function(header, footerCols) {
        header.find('> .ui-datatable-scrollable-header-box > table > thead > tr > th').each(function() {
            var headerCol = $(this),
            colIndex = headerCol.index(),
            width = headerCol.width();

            headerCol.width(width);

            if(footerCols.length > 0) {
                var footerCol = footerCols.eq(colIndex);
                footerCol.width(width);
            }
        });
    },
    
    //@Override
    updateData: function(data, clear) {
        var table = $('<table><tbody>' + data + '</tbody></table>'),
        rows = table.find('> tbody > tr'),
        empty = (clear === undefined) ? true: clear; 

        if(empty) {
            this.frozenTbody.children().remove();
            this.scrollTbody.children().remove();
        }
        
        //find slice index by checking how many rendered columns there are in frozen part
        var firstRow = this.frozenTbody.children('tr:first'),
        frozenColumnCount = firstRow.length ? firstRow.children('td').length: this.cfg.frozenColumns;

        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i),
            columns = row.children('td'),
            frozenRow = this.copyRow(row),
            scrollableRow = this.copyRow(row);
    
            frozenRow.append(columns.slice(0, frozenColumnCount));
            scrollableRow.append(columns.slice(frozenColumnCount));
            
            this.frozenTbody.append(frozenRow);
            this.scrollTbody.append(scrollableRow);
        }
        
        this.postUpdateData();
    },
    
    copyRow: function(original) {
        return $('<tr></tr>').data('ri', original.data('ri')).attr('data-rk', original.data('rk')).addClass(original.attr('class')).attr('role', 'row');
    },
    
    getThead: function() {
        return $(this.jqId + '_frozenThead,' + this.jqId + '_scrollableThead');
    },
    
    getTbody: function() {
        return $(this.jqId + '_frozenTbody,' + this.jqId + '_scrollableTbody');
    },
    
    getTfoot: function() {
        return $(this.jqId + '_frozenTfoot,' + this.jqId + '_scrollableTfoot');
    },
    
    bindRowHover: function(selector) {
        var $this = this;
        
        this.tbody.off('mouseover.datatable mouseout.datatable', selector)
                    .on('mouseover.datatable', selector, null, function() {
                        var row = $(this),
                        twinRow = $this.getTwinRow(row);
                
                        if(!row.hasClass('ui-state-highlight')) {
                            row.addClass('ui-state-hover');
                            twinRow.addClass('ui-state-hover');
                        }
                    })
                    .on('mouseout.datatable', selector, null, function() {
                        var row = $(this),
                        twinRow = $this.getTwinRow(row);

                        if(!row.hasClass('ui-state-highlight')) {
                            row.removeClass('ui-state-hover');
                            twinRow.removeClass('ui-state-hover');
                        }
                    });
    },
    
    getTwinRow: function(row) {
        var twinTbody = (this.tbody.index(row.parent()) === 0) ? this.tbody.eq(1) : this.tbody.eq(0);
        
        return twinTbody.children().eq(row.index());
    },
    
    //@Override
    highlightRow: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },
    
    //@Override
    unhighlightRow: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },
    
    //@Override
    displayExpandedRow: function(row, content) {
        var twinRow = this.getTwinRow(row);
        row.after(content);
        var expansionRow = row.next();
        expansionRow.show();
        
        twinRow.after('<tr class="ui-expanded-row-content ui-widget-content"><td></td></tr>');       
        twinRow.next().children('td').attr('colspan', twinRow.children('td').length).height(expansionRow.children('td').height());
    },
    
    //@Override
    collapseRow: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },
    
    //@Override
    getExpandedRows: function() {
        return this.frozenTbody.children('.ui-expanded-row');
    },
    
    //@Override
    showRowEditors: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },
    
    //@Override
    updateRow: function(row, content) {
        var table = $('<table><tbody>' + content + '</tbody></table>'),
        newRow = table.find('> tbody > tr'),
        columns = newRow.children('td'),
        frozenRow = this.copyRow(newRow),
        scrollableRow = this.copyRow(newRow),
        twinRow = this.getTwinRow(row);
    
        frozenRow.append(columns.slice(0, this.cfg.frozenColumns));
        scrollableRow.append(columns.slice(this.cfg.frozenColumns));

        row.replaceWith(frozenRow);
        twinRow.replaceWith(scrollableRow);
    },
    
    //@Override
    invalidateRow: function(index) {
        this.frozenTbody.children('tr').eq(index).addClass('ui-widget-content ui-row-editing ui-state-error');
        this.scrollTbody.children('tr').eq(index).addClass('ui-widget-content ui-row-editing ui-state-error');
    },
    
    //@Override
    getRowEditors: function(row) {
        return row.find('div.ui-cell-editor').add(this.getTwinRow(row).find('div.ui-cell-editor'));
    },
    
    //@Override
    findGroupResizer: function(ui) {
        var resizer = this._findGroupResizer(ui, this.frozenGroupResizers);
        if(resizer) {
            return resizer;
        }
        else {
            return this._findGroupResizer(ui, this.scrollGroupResizers);
        }
    },
    
    _findGroupResizer: function(ui, resizers) {
        for(var i = 0; i < resizers.length; i++) {
            var groupResizer = resizers.eq(i);
            if(groupResizer.offset().left === ui.helper.data('originalposition').left) {
                return groupResizer;
            }
        }
        
        return null;
    },
    
    //@Override
    addResizers: function() {
        var frozenColumns = this.frozenThead.find('> tr > th.ui-resizable-column'),
        scrollableColumns = this.scrollThead.find('> tr > th.ui-resizable-column');

        frozenColumns.prepend('<span class="ui-column-resizer">&nbsp;</span>');
        scrollableColumns.prepend('<span class="ui-column-resizer">&nbsp;</span>')
        
        if(this.cfg.resizeMode === 'fit') {
            frozenColumns.filter(':last-child').addClass('ui-frozen-column-last');
            scrollableColumns.filter(':last-child').children('span.ui-column-resizer').hide();
        }
        
        if(this.hasColumnGroup) {
            this.frozenGroupResizers = this.frozenThead.find('> tr:first > th > .ui-column-resizer');
            this.scrollGroupResizers = this.scrollThead.find('> tr:first > th > .ui-column-resizer');
        }
    },
       
    //@Override
    resize: function(event, ui) {
        var columnHeader = null,
        change = null, 
        newWidth = null, 
        nextColumnWidth = null, 
        expandMode = (this.cfg.resizeMode === 'expand');
        
        if(this.hasColumnGroup) {
            var groupResizer = this.findGroupResizer(ui);
            if(!groupResizer) {
                return;
            }
            
            columnHeader = groupResizer.parent();
        }
        else {
            columnHeader = ui.helper.parent();
        }
        
        var nextColumnHeader = columnHeader.next();
        
        var colIndex = columnHeader.index(),
        lastFrozen = columnHeader.hasClass('ui-frozen-column-last');

        if(this.cfg.liveResize) {
            change = columnHeader.outerWidth() - (event.pageX - columnHeader.offset().left),
            newWidth = (columnHeader.width() - change),
            nextColumnWidth = (nextColumnHeader.width() + change);
        } 
        else {
            change = (ui.position.left - ui.originalPosition.left),
            newWidth = (columnHeader.width() + change),
            nextColumnWidth = (nextColumnHeader.width() - change);
        }
        
        var shouldChange = (expandMode && newWidth > 15) || (lastFrozen ? (newWidth > 15) : (newWidth > 15 && nextColumnWidth > 15)); 
        if(shouldChange) {
            var frozenColumn = columnHeader.hasClass('ui-frozen-column'),
            theadClone = frozenColumn ? this.frozenTheadClone : this.scrollTheadClone,
            originalTable = frozenColumn ? this.frozenThead.parent() : this.scrollThead.parent(),
            cloneTable = theadClone.parent(),
            footerCols = frozenColumn ? this.frozenFooterCols : this.scrollFooterCols,
            footerTable = frozenColumn ? this.frozenFooterTable:  this.scrollFooterTable,
            $this = this;

            if(expandMode) {
                if(lastFrozen) {
                    this.frozenLayout.width(this.frozenLayout.width() + change);
                }
                
                //header
                originalTable.width(originalTable.width() + change);
                
                //body
                cloneTable.width(cloneTable.width() + change);
                
                //footer
                footerTable.width(footerTable.width() + change);
                
                setTimeout(function() {
                    columnHeader.width(newWidth);
                    
                    if($this.hasColumnGroup) {
                        theadClone.find('> tr:first').children('th').eq(colIndex).width(newWidth);                          //body
                        footerTable.find('> tfoot > tr:first').children('th').eq(colIndex).width(newWidth);                 //footer
                    }
                    else {
                        theadClone.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth);     //body
                        footerCols.eq(colIndex).width(newWidth);                                                            //footer
                    }
                }, 1);
                
                
            }
            else {
                if(lastFrozen) {
                    this.frozenLayout.width(this.frozenLayout.width() + change);
                }
                
                columnHeader.width(newWidth);
                nextColumnHeader.width(nextColumnWidth);
                
                if(this.hasColumnGroup) {
                    //body
                    theadClone.find('> tr:first').children('th').eq(colIndex).width(newWidth);
                    theadClone.find('> tr:first').children('th').eq(colIndex + 1).width(nextColumnWidth);

                    //footer
                    footerTable.find('> tfoot > tr:first').children('th').eq(colIndex).width(newWidth);
                    footerTable.find('> tfoot > tr:first').children('th').eq(colIndex + 1).width(nextColumnWidth);
                }
                else {
                    theadClone.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth);
                    theadClone.find(PrimeFaces.escapeClientId(nextColumnHeader.attr('id') + '_clone')).width(nextColumnWidth);

                    if(footerCols.length > 0) {
                        var footerCol = footerCols.eq(colIndex),
                        nextFooterCol = footerCol.next();

                        footerCol.width(newWidth);
                        nextFooterCol.width(nextColumnWidth);
                    }
                }
            }
        }
    },
    
    //@Override
    hasColGroup: function() {
        return this.frozenThead.children('tr').length > 1 || this.scrollThead.children('tr').length > 1;
    },
    
    //@Override
    addGhostRow: function() {
        this._addGhostRow(this.frozenTbody, this.frozenThead, this.frozenTheadClone, this.frozenFooter.find('table'), 'ui-frozen-column');
        this._addGhostRow(this.scrollTbody, this.scrollThead, this.scrollTheadClone, this.scrollFooterTable);
    },
    
    _addGhostRow: function(body, header, headerClone, footerTable, columnClass) {
        var dataColumns = body.find('tr:first').children('td'),
        dataColumnsCount = dataColumns.length,
        columnMarkup = '',
        columnStyleClass = columnClass ? 'ui-resizable-column ' + columnClass : 'ui-resizable-column';

        for(var i = 0; i < dataColumnsCount; i++) {
            columnMarkup += '<th style="height:0px;border-bottom-width: 0px;border-top-width: 0px;padding-top: 0px;padding-bottom: 0px;outline: 0 none;width:' + dataColumns.eq(i).width() + 'px" class="' + columnStyleClass + '"></th>';
        }
        
        header.prepend('<tr>' + columnMarkup + '</tr>');

        if(this.cfg.scrollable) {         
            headerClone.prepend('<tr>' + columnMarkup + '</tr>');
            footerTable.children('tfoot').prepend('<tr>' + columnMarkup + '</tr>');
        }
    }
    
}); 
/**
 * PrimeFaces Dialog Widget
 */
PrimeFaces.widget.Dialog = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.content = this.jq.children('.ui-dialog-content');
        this.titlebar = this.jq.children('.ui-dialog-titlebar');
        this.footer = this.jq.find('.ui-dialog-footer');
        this.icons = this.titlebar.children('.ui-dialog-titlebar-icon');
        this.closeIcon = this.titlebar.children('.ui-dialog-titlebar-close');
        this.minimizeIcon = this.titlebar.children('.ui-dialog-titlebar-minimize');
        this.maximizeIcon = this.titlebar.children('.ui-dialog-titlebar-maximize');
        this.blockEvents = 'focus.' + this.id + ' mousedown.' + this.id + ' mouseup.' + this.id;
        this.resizeNS = 'resize.' + this.id;
        this.cfg.absolutePositioned = this.jq.hasClass('ui-dialog-absolute');

        //configuration
        this.cfg.width = this.cfg.width||'auto';
        this.cfg.height = this.cfg.height||'auto';
        this.cfg.draggable = this.cfg.draggable === false ? false : true;
        this.cfg.resizable = this.cfg.resizable === false ? false : true;
        this.cfg.minWidth = this.cfg.minWidth||150;
        this.cfg.minHeight = this.cfg.minHeight||this.titlebar.outerHeight();
        this.cfg.position = this.cfg.position||'center';
        this.parent = this.jq.parent();

        this.initSize();

        //events
        this.bindEvents();

        if(this.cfg.draggable) {
            this.setupDraggable();
        }

        if(this.cfg.resizable){
            this.setupResizable();
        }

        if(this.cfg.modal) {
            this.syncWindowResize();
        }

        if(this.cfg.appendTo) {
        	this.jq.appendTo(PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo));
        }

        //docking zone
        if($(document.body).children('.ui-dialog-docking-zone').length === 0) {
            $(document.body).append('<div class="ui-dialog-docking-zone"></div>')
        }

        //remove related modality if there is one
        var modal = $(this.jqId + '_modal');
        if(modal.length > 0) {
            modal.remove();
        }

        //aria
        this.applyARIA();

        if(this.cfg.visible){
            this.show();
        }
    },

    //override
    refresh: function(cfg) {
        this.positionInitialized = false;
        this.loaded = false;

        $(document).off('keydown.dialog_' + cfg.id);

        if(cfg.appendTo) {
            var jqs = $('[id=' + cfg.id.replace(/:/g,"\\:") + ']');
            if(jqs.length > 1) {
            	PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(cfg.appendTo).children(this.jqId).remove();
            }
        }

        this.init(cfg);
    },

    initSize: function() {
        this.jq.css({
            'width': this.cfg.width,
            'height': 'auto'
        });

        this.content.height(this.cfg.height);

        if(this.cfg.fitViewport) {
            this.fitViewport();
        }

        //ie7 width auto width bug workaround
        if(this.cfg.width === 'auto' && PrimeFaces.isIE(7)) {
            this.jq.width(this.content.outerWidth());
        }
    },

    fitViewport: function() {
        var winHeight = $(window).height(),
        contentPadding = this.content.innerHeight() - this.content.height();

        if(this.jq.innerHeight() > winHeight) {
            this.content.height(winHeight - this.titlebar.innerHeight() - contentPadding);
        }
    },

    enableModality: function() {
        var $this = this,
        doc = $(document);

        $(document.body).append('<div id="' + this.id + '_modal" class="ui-widget-overlay"></div>')
                        .children(this.jqId + '_modal').css({
                            'width' : doc.width(),
                            'height' : doc.height(),
                            'z-index' : this.jq.css('z-index') - 1
                        });

        //Disable tabbing out of modal dialog and stop events from targets outside of dialog
        doc.on('keydown.' + this.id,
                function(event) {
                    var target = $(event.target);

                    if(event.keyCode === $.ui.keyCode.TAB) {
                        var tabbables = $this.jq.find(':tabbable').add($this.footer.find(':tabbable'));
                        if(tabbables.length) {
                            var first = tabbables.filter(':first'),
                            last = tabbables.filter(':last'),
                            focusingRadioItem = null;

                            if(first.is(':radio')) {
                                focusingRadioItem = tabbables.filter('[name="' + first.attr('name') + '"]').filter(':checked');
                                if(focusingRadioItem.length > 0) {
                                    first = focusingRadioItem;
                                }
                            }

                            if(last.is(':radio')) {
                                focusingRadioItem = tabbables.filter('[name="' + last.attr('name') + '"]').filter(':checked');
                                if(focusingRadioItem.length > 0) {
                                    last = focusingRadioItem;
                                }
                            }

                            if(target.is(document.body)) {
                                first.focus(1);
                                event.preventDefault();
                            }
                            else if(event.target === last[0] && !event.shiftKey) {
                                first.focus(1);
                                event.preventDefault();
                            }
                            else if (event.target === first[0] && event.shiftKey) {
                                last.focus(1);
                                event.preventDefault();
                            }
                        }
                    }
                    else if(!target.is(document.body) && (target.zIndex() < $this.jq.zIndex())) {
                        event.preventDefault();
                    }
                })
                .on(this.blockEvents, function(event) {
                    if ($(event.target).zIndex() < $this.jq.zIndex()) {
                        event.preventDefault();
                    }
                });
    },

    disableModality: function(){
        $(document.body).children(this.jqId + '_modal').remove();
        $(document).off(this.blockEvents).off('keydown.' + this.id);
    },

    syncWindowResize: function() {
        $(window).resize(function() {
            $(document.body).children('.ui-widget-overlay').css({
                'width': $(document).width()
                ,'height': $(document).height()
            });
        });
    },

    show: function() {
        if(this.isVisible()) {
            return;
        }

        if(!this.loaded && this.cfg.dynamic) {
            this.loadContents();
        }
        else {
            if(!this.positionInitialized) {
                this.initPosition();
            }

            this._show();
        }
    },

    _show: function() {
        this.moveToTop();
        
        //offset
        if(this.cfg.absolutePositioned) {
            var winScrollTop = $(window).scrollTop();
            this.jq.css('top', parseFloat(this.jq.css('top')) + (winScrollTop - this.lastScrollTop) + 'px');
            this.lastScrollTop = winScrollTop;
        }

        if(this.cfg.showEffect) {
            var $this = this;

            this.jq.show(this.cfg.showEffect, null, 'normal', function() {
                $this.postShow();
            });
        }
        else {
            //display dialog
            this.jq.show();

            this.postShow();
        }

        if(this.cfg.modal) {
            this.enableModality();
        }
    },

    postShow: function() {
        PrimeFaces.invokeDeferredRenders(this.id);
        
        //execute user defined callback
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }

        this.jq.attr({
            'aria-hidden': false
            ,'aria-live': 'polite'
        });

        this.applyFocus();
        
        if(this.cfg.responsive) {
            this.bindResizeListener();
        }
    },

    hide: function() {
        if(!this.isVisible()) {
            return;
        }

        if(this.cfg.hideEffect) {
            var $this = this;

            this.jq.hide(this.cfg.hideEffect, null, 'normal', function() {
                if($this.cfg.modal) {
                    $this.disableModality();
                }
                $this.onHide();
            });
        }
        else {
            this.jq.hide();
            if(this.cfg.modal) {
                this.disableModality();
            }
            this.onHide();
        }
    },

    applyFocus: function() {
        if(this.cfg.focus)
        	PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.focus).focus();
        else
            this.jq.find(':not(:submit):not(:button):not(:radio):not(:checkbox):input:visible:enabled:first').focus();
    },

    bindEvents: function() {
        var $this = this;

        //Move dialog to top if target is not a trigger for a PrimeFaces overlay
        this.jq.mousedown(function(e) {
            if(!$(e.target).data('primefaces-overlay-target')) {
                $this.moveToTop();
            }
        });

        this.icons.on('mouseover', function() {
            $(this).addClass('ui-state-hover');
        }).on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        }).on('focus', function() {
            $(this).addClass('ui-state-focus');
        }).on('blur', function() {
            $(this).removeClass('ui-state-focus');
        });

        this.closeIcon.on('click', function(e) {
            $this.hide();
            e.preventDefault();
        });

        this.maximizeIcon.click(function(e) {
            $this.toggleMaximize();
            e.preventDefault();
        });

        this.minimizeIcon.click(function(e) {
            $this.toggleMinimize();
            e.preventDefault();
        });

        if(this.cfg.closeOnEscape) {
            $(document).on('keydown.dialog_' + this.id, function(e) {
                var keyCode = $.ui.keyCode,
                active = parseInt($this.jq.css('z-index')) === PrimeFaces.zindex;

                if(e.which === keyCode.ESCAPE && $this.isVisible() && active) {
                    $this.hide();
                };
            });
        }
    },

    setupDraggable: function() {
        var $this = this;

        this.jq.draggable({
            cancel: '.ui-dialog-content, .ui-dialog-titlebar-close',
            handle: '.ui-dialog-titlebar',
            containment : 'document',
            stop: function( event, ui ) {
                if($this.hasBehavior('move')) {
                    var move = $this.cfg.behaviors['move'];
                    var ext = {
                        params: [
                            {name: $this.id + '_top', value: ui.offset.top},
                            {name: $this.id + '_left', value: ui.offset.left}
                        ]
                    };
                    move.call($this, ext);
                }
            }
        });
    },

    setupResizable: function() {
        var $this = this;

        this.jq.resizable({
            handles : 'n,s,e,w,ne,nw,se,sw',
            minWidth : this.cfg.minWidth,
            minHeight : this.cfg.minHeight,
            alsoResize : this.content,
            containment: 'document',
            start: function(event, ui) {
                $this.jq.data('offset', $this.jq.offset());

                if($this.cfg.hasIframe) {
                    $this.iframeFix = $('<div style="position:absolute;background-color:transparent;width:100%;height:100%;top:0;left:0;"></div>').appendTo($this.content);
                }
            },
            stop: function(event, ui) {
                var offset = $this.jq.data('offset');

                $this.jq.css('position', 'fixed');
                $this.jq.offset(offset);

                if($this.cfg.hasIframe) {
                    $this.iframeFix.remove();
                }
            }
        });

        this.resizers = this.jq.children('.ui-resizable-handle');
    },

    initPosition: function() {
        var $this = this;
        
        //reset
        this.jq.css({left:0,top:0});

        if(/(center|left|top|right|bottom)/.test(this.cfg.position)) {
            this.cfg.position = this.cfg.position.replace(',', ' ');

            this.jq.position({
                        my: 'center'
                        ,at: this.cfg.position
                        ,collision: 'fit'
                        ,of: window
                        //make sure dialog stays in viewport
                        ,using: function(pos) {
                            var l = pos.left < 0 ? 0 : pos.left,
                            t = pos.top < 0 ? 0 : pos.top,
                            scrollTop = $(window).scrollTop();
                    
                            //offset
                            if($this.cfg.absolutePositioned) {
                                t += scrollTop;
                                $this.lastScrollTop = scrollTop;
                            }
                            
                            $(this).css({
                                left: l
                                ,top: t
                            });
                        }
                    });
        }
        else {
            var coords = this.cfg.position.split(','),
            x = $.trim(coords[0]),
            y = $.trim(coords[1]);

            this.jq.offset({
                left: x
                ,top: y
            });
        }

        this.positionInitialized = true;
    },

    onHide: function(event, ui) {
        this.fireBehaviorEvent('close');

        this.jq.attr({
            'aria-hidden': true
            ,'aria-live': 'off'
        });

        if(this.cfg.onHide) {
            this.cfg.onHide.call(this, event, ui);
        }
        
        if(this.cfg.responsive) {
            this.unbindResizeListener();
        }
    },

    moveToTop: function() {
        this.jq.css('z-index', ++PrimeFaces.zindex);
    },

    toggleMaximize: function() {
        if(this.minimized) {
            this.toggleMinimize();
        }

        if(this.maximized) {
            this.jq.removeClass('ui-dialog-maximized');
            this.restoreState();

            this.maximizeIcon.children('.ui-icon').removeClass('ui-icon-newwin').addClass('ui-icon-extlink');
            this.maximized = false;
            
            this.fireBehaviorEvent('restoreMaximize');
        }
        else {
            this.saveState();
 
           var win = $(window);

            this.jq.addClass('ui-dialog-maximized').css({
                'width': win.width() - 6
                ,'height': win.height()
            }).offset({
                top: win.scrollTop()
                ,left: win.scrollLeft()
            });

            //maximize content
            this.content.css({
                width: 'auto',
                height: 'auto'
            });

            this.maximizeIcon.removeClass('ui-state-hover').children('.ui-icon').removeClass('ui-icon-extlink').addClass('ui-icon-newwin');
            this.maximized = true;

            this.fireBehaviorEvent('maximize');
        }
    },

    toggleMinimize: function() {
        var animate = true,
        dockingZone = $(document.body).children('.ui-dialog-docking-zone');

        if(this.maximized) {
            this.toggleMaximize();
            animate = false;
        }

        var $this = this;

        if(this.minimized) {
            this.jq.appendTo(this.parent).removeClass('ui-dialog-minimized').css({'position':'fixed', 'float':'none'});
            this.restoreState();
            this.content.show();
            this.minimizeIcon.removeClass('ui-state-hover').children('.ui-icon').removeClass('ui-icon-plus').addClass('ui-icon-minus');
            this.minimized = false;

            if(this.cfg.resizable) {
                this.resizers.show();
            }
            
            this.fireBehaviorEvent('restoreMinimize');
        }
        else {
            this.saveState();

            if(animate) {
                this.jq.effect('transfer', {
                                to: dockingZone
                                ,className: 'ui-dialog-minimizing'
                                }, 500,
                                function() {
                                    $this.dock(dockingZone);
                                    $this.jq.addClass('ui-dialog-minimized');
                                });
            }
            else {
                this.dock(dockingZone);
            }
        }
    },

    dock: function(zone) {
        zone.css('z-index', this.jq.css('z-index'));
        this.jq.appendTo(zone).css('position', 'static');
        this.jq.css({'height':'auto', 'width':'auto', 'float': 'left'});
        this.content.hide();
        this.minimizeIcon.removeClass('ui-state-hover').children('.ui-icon').removeClass('ui-icon-minus').addClass('ui-icon-plus');
        this.minimized = true;

        if(this.cfg.resizable) {
            this.resizers.hide();
        }

        this.fireBehaviorEvent('minimize');
    },

    saveState: function() {
        this.state = {
            width: this.jq.width(),
            height: this.jq.height(),
            contentWidth: this.content.width(),
            contentHeight: this.content.height()
        };

        var win = $(window);
        this.state.offset = this.jq.offset();
        this.state.windowScrollLeft = win.scrollLeft();
        this.state.windowScrollTop = win.scrollTop();
    },

    restoreState: function() {
        this.jq.width(this.state.width).height(this.state.height);
        this.content.width(this.state.contentWidth).height(this.state.contentHeight);

        var win = $(window);
        this.jq.offset({
                top: this.state.offset.top + (win.scrollTop() - this.state.windowScrollTop)
                ,left: this.state.offset.left + (win.scrollLeft() - this.state.windowScrollLeft)
        });
    },

    loadContents: function() {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.loaded = true;
                $this.show();
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    applyARIA: function() {
        this.jq.attr({
            'role': 'dialog'
            ,'aria-labelledby': this.id + '_title'
            ,'aria-hidden': !this.cfg.visible
        });

        this.titlebar.children('a.ui-dialog-titlebar-icon').attr('role', 'button');
    },

    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    },
    
    isVisible: function() {
        return this.jq.is(':visible');
    },
    
    bindResizeListener: function() {
        var $this = this;
        $(window).on(this.resizeNS, function() {
            $this.initPosition();
        });
    },
    
    unbindResizeListener: function() {
        $(window).off(this.resizeNS);
    },
    
    fireBehaviorEvent: function(event) {
        if(this.cfg.behaviors) {
            var behavior = this.cfg.behaviors[event];

            if(behavior) {
                behavior.call(this);
            }
        }
    }

});

/**
 * PrimeFaces ConfirmDialog Widget
 */
PrimeFaces.widget.ConfirmDialog = PrimeFaces.widget.Dialog.extend({

    init: function(cfg) {
        cfg.draggable = false;
        cfg.resizable = false;
        cfg.modal = true;

        if (!cfg.appendTo && cfg.global) {
        	cfg.appendTo = '@(body)';
        }

        this._super(cfg);

        this.title = this.titlebar.children('.ui-dialog-title');
        this.message = this.content.children('.ui-confirm-dialog-message');
        this.icon = this.content.children('.ui-confirm-dialog-severity');

        if(this.cfg.global) {
            PrimeFaces.confirmDialog = this;

            this.jq.find('.ui-confirmdialog-yes').on('click.ui-confirmdialog', function(e) {
                if(PrimeFaces.confirmSource) {
                    var fn = new Function('event',PrimeFaces.confirmSource.data('pfconfirmcommand'));

                    fn.call(PrimeFaces.confirmSource.get(0),e);
                    PrimeFaces.confirmDialog.hide();
                    PrimeFaces.confirmSource = null;
                }

                e.preventDefault();
            });

            this.jq.find('.ui-confirmdialog-no').on('click.ui-confirmdialog', function(e) {
                PrimeFaces.confirmDialog.hide();
                PrimeFaces.confirmSource = null;

                e.preventDefault();
            });
        }
    },

    applyFocus: function() {
        this.jq.find(':button,:submit').filter(':visible:enabled').eq(0).focus();
    },

    showMessage: function(msg) {
        var icon = (msg.icon === 'null') ? 'ui-icon-alert' : msg.icon;
        this.icon.removeClass().addClass('ui-icon ui-confirm-dialog-severity ' + icon);
        
        if(msg.header)
            this.title.text(msg.header);

        if(msg.message)
            this.message.text(msg.message);

        this.show();
    }

});

/**
 * PrimeFaces Dynamic Dialog Widget for Dialog Framework
 */ 
PrimeFaces.widget.DynamicDialog = PrimeFaces.widget.Dialog.extend({
      
    //@Override
    show: function() {
        if(this.jq.hasClass('ui-overlay-visible')) {
            return;
        }

        if(!this.positionInitialized) {
            this.initPosition();
        }

        this._show();
    },
    
    //@Override
    _show: function() {
        //replace visibility hidden with display none for effect support, toggle marker class
        this.jq.removeClass('ui-overlay-hidden').addClass('ui-overlay-visible').css({
            'display':'none'
            ,'visibility':'visible'
        });
        
        this.moveToTop();
        
        this.jq.show();

        this.postShow();

        if(this.cfg.modal) {
            this.enableModality();
        }
    }
    
});

/**
 * PrimeFaces Draggable Widget
 */
PrimeFaces.widget.Draggable = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jq = $(PrimeFaces.escapeClientId(this.cfg.target));

        if(this.cfg.appendTo) {
            this.cfg.appendTo = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo);
        }
        
        this.jq.draggable(this.cfg);
        
        this.removeScriptElement(this.id);
    }
    
});

/**
 * PrimeFaces Droppable Widget
 */
PrimeFaces.widget.Droppable = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jq = $(PrimeFaces.escapeClientId(this.cfg.target));

        this.bindDropListener();

        this.jq.droppable(this.cfg);
        
        this.removeScriptElement(this.id);
    },
    
    bindDropListener: function() {
        var _self = this;

        this.cfg.drop = function(event, ui) {
            if(_self.cfg.onDrop) {
                _self.cfg.onDrop.call(_self, event, ui);
            }
            if(_self.cfg.behaviors) {
                var dropBehavior = _self.cfg.behaviors['drop'];

                if(dropBehavior) {
                    var ext = {
                        params: [
                            {name: _self.id + '_dragId', value: ui.draggable.attr('id')},
                            {name: _self.id + '_dropId', value: _self.cfg.target}
                        ]
                    };

                    dropBehavior.call(_self, ext);
                }
            }
        };
    }
    
});
/**
 * PrimeFaces Effect Widget
 */
PrimeFaces.widget.Effect = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.source = $(PrimeFaces.escapeClientId(this.cfg.source));
        var _self = this;

        this.runner = function() {
            //avoid queuing multiple runs
            if(_self.timeoutId) {
                clearTimeout(_self.timeoutId);
            }

            _self.timeoutId = setTimeout(_self.cfg.fn, _self.cfg.delay);
        };

        if(this.cfg.event == 'load') {
            this.runner.call();
        } 
        else {
            this.source.bind(this.cfg.event, this.runner);
        }
        
        this.removeScriptElement(this.id);
    }
    
});
/**
 * PrimeFaces Fieldset Widget
 */
PrimeFaces.widget.Fieldset = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.legend = this.jq.children('.ui-fieldset-legend');

        var $this = this;

        if(this.cfg.toggleable) {
            this.content = this.jq.children('.ui-fieldset-content');
            this.toggler = this.legend.children('.ui-fieldset-toggler');
            this.stateHolder = $(this.jqId + '_collapsed');

            //Add clickable legend state behavior
            this.legend.click(function(e) {$this.toggle(e);})
                            .mouseover(function() {$this.legend.toggleClass('ui-state-hover');})
                            .mouseout(function() {$this.legend.toggleClass('ui-state-hover');})
                            .mousedown(function() {$this.legend.toggleClass('ui-state-active');})
                            .mouseup(function() {$this.legend.toggleClass('ui-state-active');})
        }
    },
    
    /**
     * Toggles the content
     */
    toggle: function(e) {
        this.updateToggleState(this.cfg.collapsed);

        var $this = this;

        this.content.slideToggle(this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            if($this.cfg.behaviors) {
                var toggleBehavior = $this.cfg.behaviors['toggle'];

                if(toggleBehavior) {
                    toggleBehavior.call($this);
                }
            }
        });
        
        PrimeFaces.invokeDeferredRenders(this.id);
    },
    
    /**
     * Updates the visual toggler state and saves state
     */
    updateToggleState: function(collapsed) {
        if(collapsed)
            this.toggler.removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
        else
            this.toggler.removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');

        this.cfg.collapsed = !collapsed;

        this.stateHolder.val(!collapsed);
    }
    
});
/**
 * PrimeFaces InputText Widget
 */
PrimeFaces.widget.InputText = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinInput(this.jq);
    },

    disable: function() {
        this.jq.prop('disabled', true).addClass('ui-state-disabled');
    },

    enable: function() {
        this.jq.prop('disabled', false).removeClass('ui-state-disabled');
    }
});

/**
 * PrimeFaces InputTextarea Widget
 */
PrimeFaces.widget.InputTextarea = PrimeFaces.widget.DeferredWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.autoResize)
            this.renderDeferred();
        else
            this._render();
    },
    
    _render: function() {
        //Visuals
        PrimeFaces.skinInput(this.jq);

        //autoComplete
        if(this.cfg.autoComplete) {
            this.setupAutoComplete();
        }
        
        //Counter
        if(this.cfg.counter) {
            this.counter = this.cfg.counter ? $(PrimeFaces.escapeClientId(this.cfg.counter)) : null;
            this.cfg.counterTemplate = this.cfg.counterTemplate||'{0}';
            this.updateCounter();
        }

        //maxLength
        if(this.cfg.maxlength) {
            this.applyMaxlength();
        }
        
        //autoResize
        if(this.cfg.autoResize) {
            this.setupAutoResize();
        }
    },

    refresh: function(cfg) {
        //remove autocomplete panel
        if(cfg.autoComplete) {
            $(PrimeFaces.escapeClientId(cfg.id + '_panel')).remove();
        }

        this.init(cfg);
    },

    setupAutoResize: function() {
        autosize(this.jq);
    },

    applyMaxlength: function() {
        var _self = this;

        if(!this.nativeMaxlengthSupported()) {
            this.jq.on('keyup.inputtextarea-maxlength', function(e) {
                var value = _self.jq.val(),
                length = value.length;

                if(length > _self.cfg.maxlength) {
                    _self.jq.val(value.substr(0, _self.cfg.maxlength));
                }
            });
        }
        
        if(_self.counter) {
            this.jq.on('keyup.inputtextarea-counter', function(e) {
                _self.updateCounter();
            });
        }
    },

    updateCounter: function() {
        var value = this.jq.val(),
        length = value.length;

        if(this.counter) {
            var remaining = this.cfg.maxlength - length;
            if(remaining < 0) {
                remaining = 0;
            }
            
            var remainingText = this.cfg.counterTemplate.replace('{0}', remaining);

            this.counter.html(remainingText);
        }
    },

    setupAutoComplete: function() {
        var panelMarkup = '<div id="' + this.id + '_panel" class="ui-autocomplete-panel ui-widget-content ui-corner-all ui-helper-hidden ui-shadow"></div>',
        _self = this;

        this.panel = $(panelMarkup).appendTo(document.body);

        this.jq.keyup(function(e) {
            var keyCode = $.ui.keyCode;

            switch(e.which) {

                case keyCode.UP:
                case keyCode.LEFT:
                case keyCode.DOWN:
                case keyCode.RIGHT:
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                case keyCode.TAB:
                case keyCode.SPACE:
                case 17: //keyCode.CONTROL:
                case 18: //keyCode.ALT:
                case keyCode.ESCAPE:
                case 224:   //mac command
                    //do not search
                break;

                default:
                    var query = _self.extractQuery();
                    if(query && query.length >= _self.cfg.minQueryLength) {

                         //Cancel the search request if user types within the timeout
                        if(_self.timeout) {
                            _self.clearTimeout(_self.timeout);
                        }

                        _self.timeout = setTimeout(function() {
                            _self.search(query);
                        }, _self.cfg.queryDelay);

                    }
                break;
            }

        }).keydown(function(e) {
            var overlayVisible = _self.panel.is(':visible'),
            keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.UP:
                case keyCode.LEFT:
                    if(overlayVisible) {
                        var highlightedItem = _self.items.filter('.ui-state-highlight'),
                        prev = highlightedItem.length == 0 ? _self.items.eq(0) : highlightedItem.prev();

                        if(prev.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            prev.addClass('ui-state-highlight');

                            if(_self.cfg.scrollHeight) {
                                PrimeFaces.scrollInView(_self.panel, prev);
                            }
                        }

                        e.preventDefault();
                    }
                    else {
                        _self.clearTimeout();
                    }
                break;

                case keyCode.DOWN:
                case keyCode.RIGHT:
                    if(overlayVisible) {
                        var highlightedItem = _self.items.filter('.ui-state-highlight'),
                        next = highlightedItem.length == 0 ? _self.items.eq(0) : highlightedItem.next();

                        if(next.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            next.addClass('ui-state-highlight');

                            if(_self.cfg.scrollHeight) {
                                PrimeFaces.scrollInView(_self.panel, next);
                            }
                        }

                        e.preventDefault();
                    }
                    else {
                        _self.clearTimeout();
                    }
                break;

                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                    if(overlayVisible) {
                        _self.items.filter('.ui-state-highlight').trigger('click');

                        e.preventDefault();
                    }
                    else {
                        _self.clearTimeout();
                    }
                break;

                case keyCode.SPACE:
                case 17: //keyCode.CONTROL:
                case 18: //keyCode.ALT:
                case keyCode.BACKSPACE:
                case keyCode.ESCAPE:
                case 224:   //mac command
                    _self.clearTimeout();

                    if(overlayVisible) {
                        _self.hide();
                    }
                break;

                case keyCode.TAB:
                    _self.clearTimeout();

                    if(overlayVisible) {
                        _self.items.filter('.ui-state-highlight').trigger('click');
                        _self.hide();
                    }
                break;
            }
        });

        //hide panel when outside is clicked
        $(document.body).bind('mousedown.ui-inputtextarea', function (e) {
            if(_self.panel.is(":hidden")) {
                return;
            }
            var offset = _self.panel.offset();
            if(e.target === _self.jq.get(0)) {
                return;
            }

            if (e.pageX < offset.left ||
                e.pageX > offset.left + _self.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + _self.panel.height()) {
                _self.hide();
            }
        });

        //Hide overlay on resize
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if(_self.panel.is(':visible')) {
                _self.hide();
            }
        });

        //dialog support
        this.setupDialogSupport();
    },

    bindDynamicEvents: function() {
        var _self = this;

        //visuals and click handler for items
        this.items.bind('mouseover', function() {
            var item = $(this);

            if(!item.hasClass('ui-state-highlight')) {
                _self.items.filter('.ui-state-highlight').removeClass('ui-state-highlight');
                item.addClass('ui-state-highlight');
            }
        })
        .bind('click', function(event) {
            var item = $(this),
            itemValue = item.attr('data-item-value'),
            insertValue = itemValue.substring(_self.query.length);

            _self.jq.focus();

            _self.jq.insertText(insertValue, _self.jq.getSelection().start, true);

            _self.invokeItemSelectBehavior(event, itemValue);

            _self.hide();
        });
    },

    invokeItemSelectBehavior: function(event, itemValue) {
        if(this.cfg.behaviors) {
            var itemSelectBehavior = this.cfg.behaviors['itemSelect'];

            if(itemSelectBehavior) {
                var ext = {
                    params : [
                        {name: this.id + '_itemSelect', value: itemValue}
                    ]
                };

                itemSelectBehavior.call(this, ext);
            }
        }
    },

    clearTimeout: function() {
        if(this.timeout) {
            clearTimeout(this.timeout);
        }

        this.timeout = null;
    },

    extractQuery: function() {
        var end = this.jq.getSelection().end,
        result = /\S+$/.exec(this.jq.get(0).value.slice(0, end)),
        lastWord = result ? result[0] : null;

        return lastWord;
    },

    search: function(query) {
        this.query = query;

        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            params: [
                {name: this.id + '_query', value: query}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.panel.html(content);
                            this.items = $this.panel.find('.ui-autocomplete-item');

                            this.bindDynamicEvents();

                            if(this.items.length > 0) {
                                //highlight first item
                                this.items.eq(0).addClass('ui-state-highlight');

                                //adjust height
                                if(this.cfg.scrollHeight && this.panel.height() > this.cfg.scrollHeight) {
                                    this.panel.height(this.cfg.scrollHeight);
                                }

                                if(this.panel.is(':hidden')) {
                                    this.show();
                                }  else {
                                    this.alignPanel(); //with new items
                                }

                            }
                            else {
                                this.panel.hide();
                            }
                        }
                    });

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    alignPanel: function() {
        var pos = this.jq.getCaretPosition(),
        offset = this.jq.offset();

        this.panel.css({
                        'left': offset.left + pos.left,
                        'top': offset.top + pos.top,
                        'width': this.jq.innerWidth(),
                        'z-index': ++PrimeFaces.zindex
                });
    },

    show: function() {
        this.alignPanel();

        this.panel.show();
    },

    hide: function() {
        this.panel.hide();
    },

    setupDialogSupport: function() {
        var dialog = this.jq.parents('.ui-dialog:first');

        if(dialog.length == 1) {
            this.panel.css('position', 'fixed');
        }
    },
    
    nativeMaxlengthSupported: function() {
        if(PrimeFaces.env.browser.msie)
            return (parseInt(PrimeFaces.env.browser.version, 10) > 9);
        else if(PrimeFaces.env.browser.opera)
            return (parseInt(PrimeFaces.env.browser.version, 10) > 12);
        else
            return true;
    }

});

/**
 * PrimeFaces SelectOneMenu Widget
 */
PrimeFaces.widget.SelectOneMenu = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.panelId = this.jqId + '_panel';
        this.input = $(this.jqId + '_input');
        this.focusInput = $(this.jqId + '_focus');
        this.label = this.jq.find('.ui-selectonemenu-label');
        this.menuIcon = this.jq.children('.ui-selectonemenu-trigger');
        this.panel = this.jq.children(this.panelId);
        this.disabled = this.jq.hasClass('ui-state-disabled');
        this.itemsWrapper = this.panel.children('.ui-selectonemenu-items-wrapper');
        this.itemsContainer = this.itemsWrapper.children('.ui-selectonemenu-items');
        this.items = this.itemsContainer.find('.ui-selectonemenu-item');
        this.options = this.input.children('option');
        this.cfg.effect = this.cfg.effect||'fade';
        this.cfg.effectSpeed = this.cfg.effectSpeed||'normal';
        this.optGroupsSize = this.itemsContainer.children('li.ui-selectonemenu-item-group').length;

        var $this = this,
        selectedOption = this.options.filter(':selected'),
        highlightedItem = this.items.eq(selectedOption.index());

        //disable options
        this.options.filter(':disabled').each(function() {
            $this.items.eq($(this).index()).addClass('ui-state-disabled');
        });

        //triggers
        this.triggers = this.cfg.editable ? this.jq.find('.ui-selectonemenu-trigger') : this.jq.find('.ui-selectonemenu-trigger, .ui-selectonemenu-label');

        //activate selected
        if(this.cfg.editable) {
            var customInputVal = this.label.val();

            //predefined input
            if(customInputVal === selectedOption.text()) {
                this.highlightItem(highlightedItem);
            }
            //custom input
            else {
                this.items.eq(0).addClass('ui-state-highlight');
                this.customInput = true;
                this.customInputVal = customInputVal;
            }
        }
        else {
            this.highlightItem(highlightedItem);
        }
        
        if(this.cfg.syncTooltip) {
            this.syncTitle(selectedOption);
        }

        //mark trigger and descandants of trigger as a trigger for a primefaces overlay
        this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        if(!this.disabled) {
            this.bindEvents();
            this.bindConstantEvents();
            this.appendPanel();
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        // see #7602
        if (PrimeFaces.env.touch) {
            this.focusInput.attr('readonly', true);
        }
        
        //for Screen Readers
        for(var i = 0; i < this.items.size(); i++) {
            this.items.eq(i).attr('id', this.id + '_' + i);
        }
        
        var highlightedItemId = highlightedItem.attr('id');
        this.focusInput.attr('aria-autocomplete', 'list')
            .attr('aria-owns', this.itemsContainer.attr('id'))
            .attr('aria-activedescendant', highlightedItemId)
            .attr('aria-describedby', highlightedItemId)
            .attr('aria-disabled', this.disabled);
        this.itemsContainer.attr('aria-activedescendant', highlightedItemId);
    },
    
    refresh: function(cfg) {
        this.panelWidthAdjusted = false;
        
        this._super(cfg);
    },

    appendPanel: function() {
        var container = this.cfg.appendTo ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo): $(document.body);

        if(!container.is(this.jq)) {
            container.children(this.panelId).remove();
            this.panel.appendTo(container);
        }
    },

    alignPanelWidth: function() {
        //align panel and container
        if(!this.panelWidthAdjusted) {
            var jqWidth = this.jq.outerWidth();
            if(this.panel.outerWidth() < jqWidth) {
                this.panel.width(jqWidth);
            }
            
            this.panelWidthAdjusted = true;
        }
    },

    bindEvents: function() {
        var $this = this;

        // Screen Reader(JAWS) hack on Chrome
        if(PrimeFaces.env.browser.webkit) {
            this.input.on('focus', function(){
                setTimeout(function(){
                    $this.focusInput.trigger('focus.ui-selectonemenu');
                },2);
            });
        }
        
        //Items
        this.items.filter(':not(.ui-state-disabled)').on('mouseover.selectonemenu', function() {
            var el = $(this);

            if(!el.hasClass('ui-state-highlight'))
                $(this).addClass('ui-state-hover');
        })
        .on('mouseout.selectonemenu', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click.selectonemenu', function() {
            $this.selectItem($(this));
            $this.changeAriaValue($(this));
        });

        //Triggers
        this.triggers.mouseenter(function() {
            if(!$this.jq.hasClass('ui-state-focus')) {
                $this.jq.addClass('ui-state-hover');
                $this.menuIcon.addClass('ui-state-hover');
            }
        })
        .mouseleave(function() {
            $this.jq.removeClass('ui-state-hover');
            $this.menuIcon.removeClass('ui-state-hover');
        })
        .click(function(e) {
            if($this.panel.is(":hidden")) {
                $this.show();
            }
            else {
                $this.hide();

                $this.revert();
                $this.changeAriaValue($this.getActiveItem());
            }

            $this.jq.removeClass('ui-state-hover');
            $this.menuIcon.removeClass('ui-state-hover');
            $this.focusInput.trigger('focus.ui-selectonemenu');
            e.preventDefault();
        });

        this.focusInput.on('focus.ui-selectonemenu', function() {
            $this.jq.addClass('ui-state-focus');
            $this.menuIcon.addClass('ui-state-focus');
        })
        .on('blur.ui-selectonemenu', function(){
            $this.jq.removeClass('ui-state-focus');
            $this.menuIcon.removeClass('ui-state-focus');
        });

        //onchange handler for editable input
        if(this.cfg.editable) {
            this.label.change(function() {
                $this.triggerChange(true);
                $this.customInput = true;
                $this.customInputVal = $(this).val();
                $this.items.filter('.ui-state-active').removeClass('ui-state-active');
                $this.items.eq(0).addClass('ui-state-active');
            });
        }

        //key bindings
        this.bindKeyEvents();

        //filter
        if(this.cfg.filter) {
            this.cfg.initialHeight = this.itemsWrapper.height();
            this.setupFilterMatcher();
            this.filterInput = this.panel.find('> div.ui-selectonemenu-filter-container > input.ui-selectonemenu-filter');
            PrimeFaces.skinInput(this.filterInput);

            this.bindFilterEvents();
        }
    },

    bindConstantEvents: function() {
        var $this = this,
        hideNS = 'mousedown.' + this.id;

        //hide overlay when outside is clicked
        $(document).off(hideNS).on(hideNS, function (e) {
            if($this.panel.is(":hidden")) {
                return;
            }

            var offset = $this.panel.offset();
            if (e.target === $this.label.get(0) ||
                e.target === $this.menuIcon.get(0) ||
                e.target === $this.menuIcon.children().get(0)) {
                return;
            }

            if (e.pageX < offset.left ||
                e.pageX > offset.left + $this.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.panel.height()) {

                $this.hide();

                $this.revert();
                $this.changeAriaValue($this.getActiveItem());
            }
        });

        this.resizeNS = 'resize.' + this.id;
        this.unbindResize();
        this.bindResize();
    },

    bindResize: function() {
        var _self = this;

        $(window).bind(this.resizeNS, function(e) {
            if(_self.panel.is(':visible')) {
                _self.alignPanel();
            }
        });
    },

    unbindResize: function() {
        $(window).unbind(this.resizeNS);
    },

    unbindEvents: function() {
        this.items.off();
        this.triggers.off();
        this.input.off();
        this.focusInput.off();
        this.label.off();
    },

    revert: function() {
        if(this.cfg.editable && this.customInput) {
            this.setLabel(this.customInputVal);
            this.items.filter('.ui-state-active').removeClass('ui-state-active');
            this.items.eq(0).addClass('ui-state-active');
        }
        else {
            this.highlightItem(this.items.eq(this.preShowValue.index()));
        }
    },

    highlightItem: function(item) {
        this.items.filter('.ui-state-highlight').removeClass('ui-state-highlight');

        if(item.length > 0) {
            item.addClass('ui-state-highlight');
            this.setLabel(item.data('label'));
        }
    },

    triggerChange: function(edited) {
        this.changed = false;

        this.input.trigger('change');

        if(!edited) {
            this.value = this.options.filter(':selected').val();
        }
    },
    
    triggerItemSelect: function() {
        if(this.cfg.behaviors) {
            var itemSelectBehavior = this.cfg.behaviors['itemSelect'];
            if(itemSelectBehavior) {
                itemSelectBehavior.call(this);
            }
        }
    },

    /**
     * Handler to process item selection with mouse
     */
    selectItem: function(item, silent) {
        var selectedOption = this.options.eq(this.resolveItemIndex(item)),
        currentOption = this.options.filter(':selected'),
        sameOption = selectedOption.val() == currentOption.val(),
        shouldChange = null;

        if(this.cfg.editable) {
            shouldChange = (!sameOption)||(selectedOption.text() != this.label.val());
        }
        else {
            shouldChange = !sameOption;
        }

        if(shouldChange) {
            this.highlightItem(item);
            this.input.val(selectedOption.val())

            this.triggerChange();

            if(this.cfg.editable) {
                this.customInput = false;
            }
            
            if(this.cfg.syncTooltip) {
                this.syncTitle(selectedOption);
            }
        }

        if(!silent) {
            this.focusInput.focus();
            this.triggerItemSelect();
        }

        if(this.panel.is(':visible')) {
            this.hide();
        }
    },
    
    syncTitle: function(option) {
        var optionTitle = this.items.eq(option.index()).attr('title');
        if(optionTitle)
            this.jq.attr('title', this.items.eq(option.index()).attr('title'));
        else
            this.jq.removeAttr('title');
    },

    resolveItemIndex: function(item) {
        if(this.optGroupsSize === 0)
            return item.index();
        else
            return item.index() - item.prevAll('li.ui-selectonemenu-item-group').length;
    },

    bindKeyEvents: function() {
        var $this = this;

        this.focusInput.on('keydown.ui-selectonemenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                case keyCode.LEFT:
                    $this.highlightPrev(e);
                break;

                case keyCode.DOWN:
                case keyCode.RIGHT:
                    $this.highlightNext(e);
                break;

                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                    $this.handleEnterKey(e);
                break;

                case keyCode.TAB:
                    $this.handleTabKey();
                break;

                case keyCode.ESCAPE:
                    $this.handleEscapeKey(e);
                break;
                
                case keyCode.SPACE:
                    $this.handleSpaceKey(e);
                break;
            }
        })
        .on('keyup.ui-selectonemenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                case keyCode.LEFT:
                case keyCode.DOWN:
                case keyCode.RIGHT:
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                case keyCode.TAB:
                case keyCode.ESCAPE:
                case keyCode.SPACE:
                case keyCode.HOME:
                case keyCode.PAGE_DOWN:
                case keyCode.PAGE_UP:
                case keyCode.END:
                case keyCode.DELETE:
                case 16: //shift
                case 17: //keyCode.CONTROL:
                case 18: //keyCode.ALT:
                case 91: //left window or cmd:
                case 92: //right window:
                case 93: //right cmd:
                case 20: //capslock:
                break;

                default:
                    var text = $(this).val(),
                    matchedOptions = null,
                    metaKey = e.metaKey||e.ctrlKey||e.shiftKey;

                    if(!metaKey) {
                        clearTimeout($this.searchTimer);

                        matchedOptions = $this.options.filter(function() {
                            return $(this).text().toLowerCase().indexOf(text.toLowerCase()) === 0;
                        });

                        if(matchedOptions.length) {
                            var highlightItem = $this.items.eq(matchedOptions.index());
                            if($this.panel.is(':hidden')) {
                                $this.selectItem(highlightItem);
                            }
                            else {
                                $this.highlightItem(highlightItem);
                                PrimeFaces.scrollInView($this.itemsWrapper, highlightItem);
                            }
                        }

                        $this.searchTimer = setTimeout(function(){
                            $this.focusInput.val('');
                        }, 1000);
                    }
                break;
            }
        });
    },

    bindFilterEvents: function() {
        var $this = this;

        this.filterInput.on('keyup.ui-selectonemenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                case keyCode.LEFT:
                case keyCode.DOWN:
                case keyCode.RIGHT:
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                case keyCode.TAB:
                case keyCode.ESCAPE:
                case keyCode.SPACE:
                case keyCode.HOME:
                case keyCode.PAGE_DOWN:
                case keyCode.PAGE_UP:
                case keyCode.END:
                case keyCode.DELETE:
                case 16: //shift
                case 17: //keyCode.CONTROL:
                case 18: //keyCode.ALT:
                case 91: //left window or cmd:
                case 92: //right window:
                case 93: //right cmd:
                case 20: //capslock:
                break;

                default:
                    var metaKey = e.metaKey||e.ctrlKey;
                    
                    if(!metaKey) {
                        $this.filter($(this).val());
                    }
                break;
            }
        })
        .on('keydown.ui-selectonemenu',function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                    $this.highlightPrev(e);
                break;

                case keyCode.DOWN:
                    $this.highlightNext(e);
                break;

                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                    $this.handleEnterKey(e);
                break;

                case keyCode.TAB:
                    $this.handleTabKey();
                break;

                case keyCode.ESCAPE:
                    $this.handleEscapeKey(e);
                break;
                
                case keyCode.SPACE:
                    $this.handleSpaceKey(e);
                break;

                default:
                break;
            }
        });
    },

    highlightNext: function(event) {
        var activeItem = this.getActiveItem(),
        next = this.panel.is(':hidden') ? activeItem.nextAll(':not(.ui-state-disabled,.ui-selectonemenu-item-group):first')
                                : activeItem.nextAll(':not(.ui-state-disabled,.ui-selectonemenu-item-group):visible:first');

        if(next.length === 1) {
            if(this.panel.is(':hidden')) {
                if(event.altKey)
                    this.show();
                else
                    this.selectItem(next);
            }
            else {
                this.highlightItem(next);
                PrimeFaces.scrollInView(this.itemsWrapper, next);
            }
            this.changeAriaValue(next);
        }

        event.preventDefault();
    },

    highlightPrev: function(event) {
        var activeItem = this.getActiveItem(),
        prev = this.panel.is(':hidden') ? activeItem.prevAll(':not(.ui-state-disabled,.ui-selectonemenu-item-group):first')
                                : activeItem.prevAll(':not(.ui-state-disabled,.ui-selectonemenu-item-group):visible:first');

        if(prev.length === 1) {
            if(this.panel.is(':hidden')) {
                this.selectItem(prev);
            }
            else {
                this.highlightItem(prev);
                PrimeFaces.scrollInView(this.itemsWrapper, prev);
            }
            this.changeAriaValue(prev);
        }

        event.preventDefault();
    },

    handleEnterKey: function(event) {
        if(this.panel.is(':visible')) {
            this.selectItem(this.getActiveItem());
        }

        event.preventDefault();
        event.stopPropagation();
    },
    
    handleSpaceKey: function(event) {
        var target = $(event.target);
        
        if(target.is('input') && target.hasClass('ui-selectonemenu-filter')) {
            return;
        }
        
        if(this.panel.is(":hidden")) {
            this.show();
        }
        else {
            this.hide();

            this.revert();
            this.changeAriaValue(this.getActiveItem());
        }
        
        event.preventDefault();
    },
    
    handleEscapeKey: function(event) {
        if(this.panel.is(':visible')) {
            this.revert();
            this.hide();
        }

        event.preventDefault();
    },

    handleTabKey: function() {
        if(this.panel.is(':visible')) {
            this.selectItem(this.getActiveItem());
        }
    },

    show: function() {
        var $this = this;
        this.alignPanel();

        this.panel.css('z-index', ++PrimeFaces.zindex);

        if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
            this.panel.parent().css('z-index', PrimeFaces.zindex - 1);
        }

        if(this.cfg.effect !== 'none') {
            this.panel.show(this.cfg.effect, {}, this.cfg.effectSpeed, function() {
                PrimeFaces.scrollInView($this.itemsWrapper, $this.getActiveItem());

                if($this.cfg.filter)
                    $this.focusFilter();
            });
        }
        else {
            this.panel.show();
            PrimeFaces.scrollInView(this.itemsWrapper, this.getActiveItem());

            if($this.cfg.filter)
                this.focusFilter(10);
        }

        //value before panel is shown
        this.preShowValue = this.options.filter(':selected');
        this.focusInput.attr('aria-expanded', true);
    },

    hide: function() {
        if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
            this.panel.parent().css('z-index', '');
        }

        this.panel.css('z-index', '').hide();
        this.focusInput.attr('aria-expanded', false);
    },

    focus: function() {
        this.focusInput.focus();
    },

    focusFilter: function(timeout) {
        if(timeout) {
            var $this = this;
            setTimeout(function() {
                $this.focusFilter();
            }, timeout);
        }
        else {
            this.filterInput.focus();
        }
    },

    blur: function() {
        this.focusInput.blur();
    },

    disable: function() {
    	if (!this.disabled) {
	        this.disabled = true;
	        this.jq.addClass('ui-state-disabled');
	        this.input.attr('disabled', 'disabled');
	        if(this.cfg.editable) {
	            this.label.attr('disabled', 'disabled');
	        }
	        this.unbindEvents();
    	}
    },

    enable: function() {
    	if (this.disabled) {
	        this.disabled = false;
	        this.jq.removeClass('ui-state-disabled');
	        this.input.removeAttr('disabled');
	        if(this.cfg.editable) {
	            this.label.removeAttr('disabled');
	        }
	        this.bindEvents();
    	}
    },

    alignPanel: function() {
        this.alignPanelWidth();
        
        if(this.panel.parent().is(this.jq)) {
            this.panel.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.panel.css({left:'', top:''}).position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.jq
                ,collision: 'flipfit'
            });
        }
    },

    setLabel: function(value) {
        var displayedLabel = this.getLabelToDisplay(value);
        
        if(this.cfg.editable) {
            if(value === '&nbsp;')
                this.label.val('');
            else
                this.label.val(displayedLabel);
        }
        else {
            if(value === '&nbsp;')
                this.label.html('&nbsp;');
            else
                this.label.text(displayedLabel);
        }
    },

    selectValue : function(value) {
        var option = this.options.filter('[value="' + value + '"]');

        this.selectItem(this.items.eq(option.index()), true);
    },

    getActiveItem: function() {
        return this.items.filter('.ui-state-highlight');
    },

    setupFilterMatcher: function() {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode||'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            ,'contains': this.containsFilter
            ,'endsWith': this.endsWithFilter
            ,'custom': this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    },

    startsWithFilter: function(value, filter) {
        return value.indexOf(filter) === 0;
    },

    containsFilter: function(value, filter) {
        return value.indexOf(filter) !== -1;
    },

    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    filter: function(value) {
        this.cfg.initialHeight = this.cfg.initialHeight||this.itemsWrapper.height();
        var filterValue = this.cfg.caseSensitive ? $.trim(value) : $.trim(value).toLowerCase();

        if(filterValue === '') {
            this.items.filter(':hidden').show();
            this.itemsContainer.children('.ui-selectonemenu-item-group').show();
        }
        else {
            for(var i = 0; i < this.options.length; i++) {
                var option = this.options.eq(i),
                itemLabel = this.cfg.caseSensitive ? option.text() : option.text().toLowerCase(),
                item = this.items.eq(i);

                if(item.hasClass('ui-noselection-option')) {
                    item.hide();
                }
                else {
                    if(this.filterMatcher(itemLabel, filterValue))
                        item.show();
                    else
                        item.hide();
                }
            }
            
            //Toggle groups
            var groups = this.itemsContainer.children('.ui-selectonemenu-item-group');
            for(var g = 0; g < groups.length; g++) {
                var group = groups.eq(g);
                
                if(g === (groups.length - 1)) {
                    if(group.nextAll().filter(':visible').length === 0)
                        group.hide();
                    else
                        group.show();
                }
                else {
                    if(group.nextUntil('.ui-selectonemenu-item-group').filter(':visible').length === 0)
                        group.hide();
                    else
                        group.show();
                }
            }
        }

        var firstVisibleItem = this.items.filter(':visible:first');
        if(firstVisibleItem.length) {
            this.highlightItem(firstVisibleItem);
        }

        if(this.itemsContainer.height() < this.cfg.initialHeight) {
            this.itemsWrapper.css('height', 'auto');
        }
        else {
            this.itemsWrapper.height(this.cfg.initialHeight);
        }

        this.alignPanel();
    },

    getSelectedValue: function() {
        return this.input.val();
    },

    getSelectedLabel: function() {
        return this.options.filter(':selected').text();
    },
    
    getLabelToDisplay: function(value) {
        if(this.cfg.labelTemplate && value !== '&nbsp;') {
            return this.cfg.labelTemplate.replace('{0}', value);
        }
        return value;
    },
    
    changeAriaValue: function (item) {
        var itemId = item.attr('id');

        this.focusInput.attr('aria-activedescendant', itemId)
                .attr('aria-describedby', itemId);
        this.itemsContainer.attr('aria-activedescendant', itemId);
    }       

});

/**
 * PrimeFaces SelectOneRadio Widget
 */
PrimeFaces.widget.SelectOneRadio = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        //custom layout
        if(this.cfg.custom) {
            this.originalInputs = this.jq.find(':radio');
            this.inputs = $('input:radio[name="' + this.id + '"].ui-radio-clone');
            this.outputs = this.inputs.parent().next('.ui-radiobutton-box');
            this.labels = $();

            //labels
            for(var i=0; i < this.outputs.length; i++) {
                this.labels = this.labels.add('label[for="' + this.outputs.eq(i).parent().attr('id') + '"]');
            }
            
            //update radio state
            for(var i = 0; i < this.inputs.length; i++) {
                var input = this.inputs.eq(i),
                itemindex = input.data('itemindex'),
                original = this.originalInputs.eq(itemindex);
        
                input.val(original.val());
                
                if(original.is(':checked')) {
                    input.prop('checked', true).parent().next().addClass('ui-state-active').children('.ui-radiobutton-icon')
                            .addClass('ui-icon-bullet').removeClass('ui-icon-blank');
                }
            }
        }
        //regular layout
        else {
            this.outputs = this.jq.find('.ui-radiobutton-box');
            this.inputs = this.jq.find(':radio');       
            this.labels = this.jq.find('label');
        }

        this.enabledInputs = this.inputs.filter(':not(:disabled)');
        this.checkedRadio = this.outputs.filter('.ui-state-active');

        this.bindEvents();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    bindEvents: function() {
        var $this = this;

        this.outputs.filter(':not(.ui-state-disabled)').on('mouseover.selectOneRadio', function() {
            $(this).addClass('ui-state-hover');
        })
        .on('mouseout.selectOneRadio', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click.selectOneRadio', function() {
            var radio = $(this),
            input = radio.prev().children(':radio');

            if(!radio.hasClass('ui-state-active')) {
                $this.unselect($this.checkedRadio);
                $this.select(radio);
                input.trigger('click');
                input.trigger('change');
            }
            else {
                input.trigger('click');
            }
        });

        this.labels.filter(':not(.ui-state-disabled)').on('click.selectOneRadio', function(e) {
            var target = $(PrimeFaces.escapeClientId($(this).attr('for'))),
            radio = null;

            //checks if target is input or not(custom labels)
            if(target.is(':input'))
                radio = target.parent().next();
            else
                radio = target.children('.ui-radiobutton-box'); //custom layout

            radio.trigger('click.selectOneRadio');

            e.preventDefault();
        });

        this.enabledInputs.on('focus.selectOneRadio', function() {
            var input = $(this),
            radio = input.parent().next();

            if(input.prop('checked')) {
                radio.removeClass('ui-state-active');
            }

            radio.addClass('ui-state-focus');
        })
        .on('blur.selectOneRadio', function() {
            var input = $(this),
            radio = input.parent().next();

            if(input.prop('checked')) {
                radio.addClass('ui-state-active');
            }

            radio.removeClass('ui-state-focus');
        })
        .on('keydown.selectOneRadio', function(e) {
            var input = $(this),
            currentRadio = input.parent().next(),
            index = $this.enabledInputs.index(input),
            size = $this.enabledInputs.length,
            keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                case keyCode.LEFT:
                    var prevRadioInput = (index === 0) ? $this.enabledInputs.eq((size - 1)) : $this.enabledInputs.eq(--index),
                    prevRadio = prevRadioInput.parent().next();

                    input.blur();
                    $this.unselect(currentRadio);
                    $this.select(prevRadio);
                    prevRadioInput.trigger('focus').trigger('change');
                    e.preventDefault();
                break;

                case keyCode.DOWN:
                case keyCode.RIGHT:
                    var nextRadioInput = (index === (size - 1)) ? $this.enabledInputs.eq(0) : $this.enabledInputs.eq(++index),
                    nextRadio = nextRadioInput.parent().next();

                    input.blur();
                    $this.unselect(currentRadio);
                    $this.select(nextRadio);
                    nextRadioInput.trigger('focus').trigger('change');
                    e.preventDefault();
                break;

                case keyCode.SPACE:
                    input.blur();
                    if(!input.prop('checked')) {
                        $this.select(currentRadio);
                    }

                    e.preventDefault();
                break;
            }
        });
    },

    unselect: function(radio) {
        radio.prev().children(':radio').prop('checked', false);
        radio.removeClass('ui-state-active').children('.ui-radiobutton-icon').removeClass('ui-icon-bullet').addClass('ui-icon-blank');
    },

    select: function(radio) {
        this.checkedRadio = radio;
        radio.addClass('ui-state-active').children('.ui-radiobutton-icon').addClass('ui-icon-bullet').removeClass('ui-icon-blank');
        radio.prev().children(':radio').prop('checked', true);
    }

});

/**
 * PrimeFaces SelectBooleanCheckbox Widget
 */
PrimeFaces.widget.SelectBooleanCheckbox = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.box = this.jq.find('.ui-chkbox-box');
        this.icon = this.box.children('.ui-chkbox-icon');
        this.itemLabel = this.jq.find('.ui-chkbox-label');
        this.disabled = this.input.is(':disabled');

        var $this = this;

        //bind events if not disabled
        if(!this.disabled) {
            this.box.on('mouseover.selectBooleanCheckbox', function() {
                $this.box.addClass('ui-state-hover');
            })
            .on('mouseout.selectBooleanCheckbox', function() {
                $this.box.removeClass('ui-state-hover');
            })
            .on('click.selectBooleanCheckbox', function() {
                $this.toggle();
            });

            this.input.on('focus.selectBooleanCheckbox', function() {
                if($(this).prop('checked')) {
                    $this.box.removeClass('ui-state-active');
                }

                $this.box.addClass('ui-state-focus');
            })
            .on('blur.selectBooleanCheckbox', function() {
                if($(this).prop('checked')) {
                    $this.box.addClass('ui-state-active');
                }

                $this.box.removeClass('ui-state-focus');
            })
            .on('keydown.selectBooleanCheckbox', function(e) {
                var keyCode = $.ui.keyCode;
                if(e.which === keyCode.SPACE) {
                    e.preventDefault();
                }
            })
            .on('keyup.selectBooleanCheckbox', function(e) {
                var keyCode = $.ui.keyCode;
                if(e.which === keyCode.SPACE) {
                    $this.toggle();
                    $this.input.trigger('focus');

                    e.preventDefault();
                }
            });

            //toggle state on label click
            this.itemLabel.click(function() {
                $this.toggle();
                $this.input.trigger('focus');
            });
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    toggle: function() {
        if(this.isChecked())
            this.uncheck();
        else
            this.check();
    },

    isChecked: function() {
        return this.input.prop('checked');
    },

    check: function() {
        if(!this.isChecked()) {
            this.input.prop('checked', true).trigger('change');
            this.input.attr('aria-checked', true);
            this.box.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
        }
    },

    uncheck: function() {
        if(this.isChecked()) {
            this.input.prop('checked', false).trigger('change');
            this.input.attr('aria-checked', false);
            this.box.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
        }
    }

});

/**
 * PrimeFaces SelectManyCheckbox Widget
 */
PrimeFaces.widget.SelectManyCheckbox = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.custom) {
            this.originalInputs = this.jq.find(':checkbox');
            this.inputs = $('input:checkbox[name="' + this.id + '"].ui-chkbox-clone');
            this.outputs = this.inputs.parent().next('.ui-chkbox-box');

            //update checkbox state
            for(var i = 0; i < this.inputs.length; i++) {
                var input = this.inputs.eq(i),
                itemindex = input.data('itemindex'),
                original = this.originalInputs.eq(itemindex);

                input.val(original.val());

                if(original.is(':checked')) {
                    input.prop('checked', true).parent().next().addClass('ui-state-active').children('.ui-chkbox-icon')
                            .addClass('ui-icon-check').removeClass('ui-icon-blank');
                }
            }
        }
        else {
            this.outputs = this.jq.find('.ui-chkbox-box:not(.ui-state-disabled)');
            this.inputs = this.jq.find(':checkbox:not(:disabled)');
        }

        this.enabledInputs = this.inputs.filter(':not(:disabled)');

        this.bindEvents();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    bindEvents: function() {
        this.outputs.filter(':not(.ui-state-disabled)').on('mouseover', function() {
            $(this).addClass('ui-state-hover');
        })
        .on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click', function() {
            var checkbox = $(this),
            input = checkbox.prev().children(':checkbox');

            input.trigger('click');

            if($.browser.msie && parseInt($.browser.version) < 9) {
                input.trigger('change');
            }
        });

        //delegate focus-blur-change states
        this.enabledInputs.on('focus', function() {
            var input = $(this),
            checkbox = input.parent().next();

            if(input.prop('checked')) {
                checkbox.removeClass('ui-state-active');
            }

            checkbox.addClass('ui-state-focus');
        })
        .on('blur', function() {
            var input = $(this),
            checkbox = input.parent().next();

            if(input.prop('checked')) {
                checkbox.addClass('ui-state-active');
            }

            checkbox.removeClass('ui-state-focus');
        })
        .on('change', function(e) {
            var input = $(this),
            checkbox = input.parent().next(),
            hasFocus = input.is(':focus'),
            disabled = input.is(':disabled');

            if(disabled) {
                return;
            }

            if(input.is(':checked')) {
                checkbox.children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');

                if(!hasFocus) {
                    checkbox.addClass('ui-state-active');
                }
            }
            else {
                checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
            }
        });
    }

});

/**
 * PrimeFaces SelectListbox Widget
 */
PrimeFaces.widget.SelectListbox = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input'),
        this.listContainer = this.jq.children('.ui-selectlistbox-listcontainer');
        this.listElement = this.listContainer.children('.ui-selectlistbox-list');
        this.options = $(this.input).children('option');
        this.allItems = this.listElement.find('.ui-selectlistbox-item');
        this.items = this.allItems.filter(':not(.ui-state-disabled)');

        //scroll to selected
        var selected = this.options.filter(':selected:not(:disabled)');
        if(selected.length) {
            PrimeFaces.scrollInView(this.listContainer, this.items.eq(selected.eq(0).index()));
        }

        this.bindEvents();

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    bindEvents: function() {
        var $this = this;

        //items
        this.items.on('mouseover.selectListbox', function() {
            var item = $(this);
            if(!item.hasClass('ui-state-highlight')) {
                item.addClass('ui-state-hover');
            }
        })
        .on('mouseout.selectListbox', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('dblclick.selectListbox', function(e) {
            $this.input.trigger('dblclick');

            PrimeFaces.clearSelection();
            e.preventDefault();
        });

        //input
        this.input.on('focus.selectListbox', function() {
            $this.jq.addClass('ui-state-focus');
        }).on('blur.selectListbox', function() {
            $this.jq.removeClass('ui-state-focus');
        });

        if(this.cfg.filter) {
            this.filterInput = this.jq.find('> div.ui-selectlistbox-filter-container > input.ui-selectlistbox-filter');
            PrimeFaces.skinInput(this.filterInput);
            this.filterInput.on('keyup.selectListbox', function(e) {
                $this.filter(this.value);
            });

            this.setupFilterMatcher();
        }
    },

    unselectAll: function() {
        this.items.removeClass('ui-state-highlight ui-state-hover');
        this.options.filter(':selected').prop('selected', false);
    },

    selectItem: function(item) {
        item.addClass('ui-state-highlight').removeClass('ui-state-hover');
        this.options.eq(item.index()).prop('selected', true);
    },

    unselectItem: function(item) {
        item.removeClass('ui-state-highlight');
        this.options.eq(item.index()).prop('selected', false);
    },

    setupFilterMatcher: function() {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode||'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            ,'contains': this.containsFilter
            ,'endsWith': this.endsWithFilter
            ,'custom': this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    },

    startsWithFilter: function(value, filter) {
        return value.indexOf(filter) === 0;
    },

    containsFilter: function(value, filter) {
        return value.indexOf(filter) !== -1;
    },

    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    filter: function(value) {
        var filterValue = this.cfg.caseSensitive ? $.trim(value) : $.trim(value).toLowerCase();

        if(filterValue === '') {
            this.items.filter(':hidden').show();
        }
        else {
            for(var i = 0; i < this.options.length; i++) {
                var option = this.options.eq(i),
                itemLabel = this.cfg.caseSensitive ? option.text() : option.text().toLowerCase(),
                item = this.items.eq(i);

                if(this.filterMatcher(itemLabel, filterValue))
                    item.show();
                else
                    item.hide();
            }
        }
    }
});

/**
 * PrimeFaces SelectOneListbox Widget
 */
PrimeFaces.widget.SelectOneListbox = PrimeFaces.widget.SelectListbox.extend({

    bindEvents: function() {
        this._super();
        var $this = this;
        
        if(!this.cfg.disabled) {
            this.items.on('click.selectListbox', function(e) {
                var item = $(this),
                selectedItem = $this.items.filter('.ui-state-highlight');

                if(item.index() !== selectedItem.index()) {
                    if(selectedItem.length) {
                        $this.unselectItem(selectedItem);
                    }

                    $this.selectItem(item);
                    $this.input.trigger('change');
                }

                $this.input.trigger('click');

                PrimeFaces.clearSelection();
                e.preventDefault();
            });
        }
    }
});

/**
 * PrimeFaces SelectManyMenu Widget
 */
PrimeFaces.widget.SelectManyMenu = PrimeFaces.widget.SelectListbox.extend({

    bindEvents: function() {
        this._super();
        var $this = this;

        if(!this.cfg.disabled) {
            this.items.on('click.selectListbox', function(e) {
                //stop propagation
                if($this.checkboxClick) {
                    $this.checkboxClick = false;
                    return;
                }

                var item = $(this),
                selectedItems = $this.items.filter('.ui-state-highlight'),
                metaKey = (e.metaKey||e.ctrlKey),
                unchanged = (!metaKey && selectedItems.length === 1 && selectedItems.index() === item.index());

                if(!e.shiftKey) {
                    if(!metaKey) {
                        $this.unselectAll();
                    }

                    if(metaKey && item.hasClass('ui-state-highlight')) {
                        $this.unselectItem(item);
                    }
                    else {
                        $this.selectItem(item);
                        $this.cursorItem = item;
                    }
                }
                else {
                    //range selection
                    if($this.cursorItem) {
                        $this.unselectAll();

                        var currentItemIndex = item.index(),
                        cursorItemIndex = $this.cursorItem.index(),
                        startIndex = (currentItemIndex > cursorItemIndex) ? cursorItemIndex : currentItemIndex,
                        endIndex = (currentItemIndex > cursorItemIndex) ? (currentItemIndex + 1) : (cursorItemIndex + 1);

                        for(var i = startIndex ; i < endIndex; i++) {
                            var it = $this.allItems.eq(i);

                            if(it.is(':visible') && !it.hasClass('ui-state-disabled')) {
                                $this.selectItem(it);
                            }
                        }
                    }
                    else {
                        $this.selectItem(item);
                        $this.cursorItem = item;
                    }
                }

                if(!unchanged) {
                    $this.input.trigger('change');
                }

                $this.input.trigger('click');
                PrimeFaces.clearSelection();
                e.preventDefault();
            });

            if(this.cfg.showCheckbox) {
                this.checkboxes = this.jq.find('div.ui-chkbox > div.ui-chkbox-box');

                this.checkboxes.on('mouseover.selectManyMenu', function(e) {
                    var chkbox = $(this);

                    if(!chkbox.hasClass('ui-state-active'))
                        chkbox.addClass('ui-state-hover');
                })
                .on('mouseout.selectManyMenu', function(e) {
                    $(this).removeClass('ui-state-hover');
                })
                .on('click.selectManyMenu', function(e) {
                    $this.checkboxClick = true;

                    var item = $(this).closest('.ui-selectlistbox-item');
                    if(item.hasClass('ui-state-highlight'))
                        $this.unselectItem(item);
                    else
                        $this.selectItem(item);

                    $this.input.trigger('change');
                });
            }
        }
    },

    unselectAll: function() {
        for(var i = 0; i < this.items.length; i++) {
            this.unselectItem(this.items.eq(i));
        }
    },

    selectItem: function(item) {
        this._super(item);

        if(this.cfg.showCheckbox) {
            this.selectCheckbox(item.find('div.ui-chkbox-box'));
        }
    },

    unselectItem: function(item) {
        this._super(item);

        if(this.cfg.showCheckbox) {
            this.unselectCheckbox(item.find('div.ui-chkbox-box'));
        }
    },

    selectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-hover').addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
    },

    unselectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
    }
});

/**
 * PrimeFaces CommandButton Widget
 */
PrimeFaces.widget.CommandButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinButton(this.jq);
    },

    disable: function() {
        this.jq.removeClass('ui-state-hover ui-state-focus ui-state-active')
                .addClass('ui-state-disabled').attr('disabled', 'disabled');
    },

    enable: function() {
        this.jq.removeClass('ui-state-disabled').removeAttr('disabled');
    }

});

/*
 * PrimeFaces Button Widget
 */
PrimeFaces.widget.Button = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinButton(this.jq);
    },

    disable: function() {
        this.jq.removeClass('ui-state-hover ui-state-focus ui-state-active')
                .addClass('ui-state-disabled').attr('disabled', 'disabled');
    },

    enable: function() {
        this.jq.removeClass('ui-state-disabled').removeAttr('disabled');
    }

});

/**
 * PrimeFaces SelecyManyButton Widget
 */
PrimeFaces.widget.SelectManyButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.buttons = this.jq.children('div:not(.ui-state-disabled)');
        this.inputs = this.jq.find(':checkbox:not(:disabled)');
        var $this = this;
                
        this.buttons.mouseover(function() {
            var button = $(this);
            if(!button.hasClass('ui-state-active'))
                button.addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            var button = $(this);

            if(button.hasClass('ui-state-active'))
                $this.unselect(button);
            else
                $this.select(button);
        });

        /* For keyboard accessibility */
        this.buttons.on('focus.selectManyButton', function(){
            var button = $(this),
            checkbox = button.children(':checkbox');
        
            if(checkbox.prop('checked')) { 
                button.removeClass('ui-state-active');
            }
            
            button.addClass('ui-state-focus');
        })
        .on('blur.selectManyButton', function(){
            var button = $(this),
            checkbox = button.children(':checkbox');
    
            if(checkbox.prop('checked')) { 
                button.addClass('ui-state-active');
            }
            
            button.removeClass('ui-state-focus');
        })
        .on('keydown.selectManyButton', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                var button = $(this),
                checkbox = button.children(':checkbox');
                
                if(checkbox.prop('checked')) {
                    $this.unselect(button);
                    button.removeClass('ui-state-hover');
                }
                else {
                    $this.select(button);
                }
                e.preventDefault();
            }  
        });
        
        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    select: function(button) {
        button.removeClass('ui-state-hover').addClass('ui-state-active')
                                .children(':checkbox').prop('checked', true).change();

    },

    unselect: function(button) {
        button.removeClass('ui-state-active').addClass('ui-state-hover')
                                .children(':checkbox').prop('checked', false).change();
    }

});

/**
 * PrimeFaces SelectOneButton Widget
 */
PrimeFaces.widget.SelectOneButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.buttons = this.jq.children('div:not(.ui-state-disabled)');
        this.inputs = this.jq.find(':radio:not(:disabled)');
                
        this.bindEvents();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    bindEvents: function() {
        var $this = this;

        this.buttons.on('mouseover', function() {
            var button = $(this);
            if(!button.hasClass('ui-state-active')) {
                button.addClass('ui-state-hover');
            }
        })
        .on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click', function() {
            var button = $(this);

            if(!button.hasClass('ui-state-active')) {
                $this.select(button);
            }
        });
        
        /* For keyboard accessibility */
        this.buttons.on('focus.selectOneButton', function(){
            var button = $(this),
            radio = button.children(':radio');
        
            if(radio.prop('checked')) { 
                button.removeClass('ui-state-active');
            }
            
            button.addClass('ui-state-focus');
        })
        .on('blur.selectOneButton', function(){
            var button = $(this),
            radio = button.children(':radio');
    
            if(radio.prop('checked')) { 
                button.addClass('ui-state-active');
            }
            
            button.removeClass('ui-state-focus');
        })
        .on('keydown.selectOneButton', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                var button = $(this),
                radio = button.children(':radio');
                
                if(!radio.prop('checked')) {
                    $this.select(button);
                }
                e.preventDefault();
            }  
        });
    },

    select: function(button) {
        this.buttons.filter('.ui-state-active').removeClass('ui-state-active ui-state-hover').children(':radio').prop('checked', false);

        button.addClass('ui-state-active').children(':radio').prop('checked', true).change();
    }

});

/**
 * PrimeFaces SelectBooleanButton Widget
 */
PrimeFaces.widget.SelectBooleanButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.disabled = this.input.is(':disabled');
        this.icon = this.jq.children('.ui-button-icon-left');
        var $this = this;

        //bind events if not disabled
        if(!this.disabled) {
            this.jq.on('mouseover', function() {
                if(!$this.jq.hasClass('ui-state-active')) {
                    $this.jq.addClass('ui-state-hover');
                }
            }).on('mouseout', function() {
                $this.jq.removeClass('ui-state-hover');
            })
            .on('click', function() {
                $this.toggle();
                $this.input.trigger('focus');
            });
        }
        
        this.input.on('focus', function() {            
            $this.jq.addClass('ui-state-focus');
        })
        .on('blur', function() {            
            $this.jq.removeClass('ui-state-focus');
        })
        .on('keydown', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                e.preventDefault();
            }
        })
        .on('keyup', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                $this.toggle();

                e.preventDefault();
            }
        });

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    toggle: function() {
        if(!this.disabled) {
            if(this.input.prop('checked'))
                this.uncheck();
            else
                this.check();
        }
    },

    check: function() {
        if(!this.disabled) {
            this.input.prop('checked', true);
            this.jq.addClass('ui-state-active').children('.ui-button-text').html(this.cfg.onLabel);

            if(this.icon.length > 0) {
                this.icon.removeClass(this.cfg.offIcon).addClass(this.cfg.onIcon);
            }

            this.input.trigger('change');
        }
    },

    uncheck: function() {
        if(!this.disabled) {
            this.input.prop('checked', false);
            this.jq.removeClass('ui-state-active').children('.ui-button-text').html(this.cfg.offLabel);

            if(this.icon.length > 0) {
                this.icon.removeClass(this.cfg.onIcon).addClass(this.cfg.offIcon);
            }

            this.input.trigger('change');
        }
    }

});

/**
 * PrimeFaces SelectCheckboxMenu Widget
 */
PrimeFaces.widget.SelectCheckboxMenu = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.labelContainer = this.jq.find('.ui-selectcheckboxmenu-label-container');
        this.label = this.jq.find('.ui-selectcheckboxmenu-label');
        this.menuIcon = this.jq.children('.ui-selectcheckboxmenu-trigger');
        this.triggers = this.jq.find('.ui-selectcheckboxmenu-trigger, .ui-selectcheckboxmenu-label');
        this.disabled = this.jq.hasClass('ui-state-disabled');
        this.inputs = this.jq.find(':checkbox');
        this.panelId = this.id + '_panel';
        this.keyboardTarget = $(this.jqId + '_focus');
        this.tabindex = this.keyboardTarget.attr('tabindex');
        this.cfg.showHeader = (this.cfg.showHeader === undefined) ? true : this.cfg.showHeader;
        
        if(!this.disabled) {
            this.renderPanel();
            
            if(this.tabindex) {
                this.panel.find('a, input').attr('tabindex', this.tabindex);  
            }
            
            this.checkboxes = this.itemContainer.find('.ui-chkbox-box:not(.ui-state-disabled)');
            this.labels = this.itemContainer.find('label');

            this.bindEvents();
            
            //mark trigger and descandants of trigger as a trigger for a primefaces overlay
            this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
        }

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    refresh: function(cfg) {
        $(PrimeFaces.escapeClientId(this.panelId)).remove();

        this.init(cfg);
    },

    renderPanel: function() {
        this.panel = $('<div id="' + this.panelId + '" class="ui-selectcheckboxmenu-panel ui-widget ui-widget-content ui-corner-all ui-helper-hidden"></div>');

        this.appendPanel();

        if(this.cfg.panelStyle) {
            this.panel.attr('style', this.cfg.panelStyle);
        }

        if(this.cfg.panelStyleClass) {
            this.panel.addClass(this.cfg.panelStyleClass);
        }

        this.renderHeader();

        this.renderItems();

        if(this.cfg.scrollHeight) {
            this.itemContainerWrapper.height(this.cfg.scrollHeight);
        }
        else if(this.inputs.length > 10) {
            this.itemContainerWrapper.height(200)
        }
    },

    renderHeader: function() {
        this.header = $('<div class="ui-widget-header ui-corner-all ui-selectcheckboxmenu-header ui-helper-clearfix"></div>')
                        .appendTo(this.panel);

        if(!this.cfg.showHeader) {
            this.header.removeClass('ui-helper-clearfix').addClass('ui-helper-hidden');
        }
        //toggler
        this.toggler = $('<div class="ui-chkbox ui-widget"><div class="ui-helper-hidden-accessible"><input type="checkbox" readonly="readonly"/></div><div class="ui-chkbox-box ui-widget ui-corner-all ui-state-default"><span class="ui-chkbox-icon ui-icon ui-icon-blank"></span></div></div>')
                            .appendTo(this.header);
        this.togglerBox = this.toggler.children('.ui-chkbox-box');
        if(this.inputs.filter(':not(:checked)').length === 0) {
            this.check(this.togglerBox);
        }

        //filter
        if(this.cfg.filter) {
            this.filterInputWrapper = $('<div class="ui-selectcheckboxmenu-filter-container"></div>').appendTo(this.header);
            this.filterInput = $('<input type="text" aria-multiline="false" aria-readonly="false" aria-disabled="false" role="textbox" class="ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all">')
                                .appendTo(this.filterInputWrapper);

            this.filterInputWrapper.append("<span class='ui-icon ui-icon-search'></span>");
        }

        //closer
        this.closer = $('<a class="ui-selectcheckboxmenu-close ui-corner-all" href="#"><span class="ui-icon ui-icon-circle-close"></span></a>')
                    .appendTo(this.header);

    },

    renderItems: function() {
        var $this = this;

        this.itemContainerWrapper = $('<div class="ui-selectcheckboxmenu-items-wrapper"><ul class="ui-selectcheckboxmenu-items ui-selectcheckboxmenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset"></ul></div>')
                .appendTo(this.panel);

        this.itemContainer = this.itemContainerWrapper.children('ul.ui-selectcheckboxmenu-items');

        this.inputs.each(function() {
            var input = $(this),
            label = input.next(),
            disabled = input.is(':disabled'),
            checked = input.is(':checked'),
            title = input.attr('title'),
            boxClass = 'ui-chkbox-box ui-widget ui-corner-all ui-state-default',
            itemClass = 'ui-selectcheckboxmenu-item ui-selectcheckboxmenu-list-item ui-corner-all',
            escaped = input.data('escaped');

            if(disabled) {
                boxClass += " ui-state-disabled";
            }

            if(checked) {
                boxClass += " ui-state-active";
            }

            var iconClass = checked ? 'ui-chkbox-icon ui-icon ui-icon-check' : 'ui-chkbox-icon ui-icon ui-icon-blank',
            itemClass = checked ? itemClass + ' ui-selectcheckboxmenu-checked' : itemClass + ' ui-selectcheckboxmenu-unchecked';

            var item = $('<li class="' + itemClass + '"></li>');
            item.append('<div class="ui-chkbox ui-widget"><div class="ui-helper-hidden-accessible"><input type="checkbox" readonly="readonly"></input></div>' +
                    '<div class="' + boxClass + '"><span class="' + iconClass + '"></span></div></div>');
            
            var itemLabel = $('<label></label>');
            if(escaped)
                itemLabel.text(label.text());
            else
                itemLabel.html(label.html());
            
            itemLabel.appendTo(item);
        
            if(title) {
                item.attr('title', title);
            }

            item.find('> .ui-chkbox > .ui-helper-hidden-accessible > input').prop('checked', checked);

            $this.itemContainer.append(item);
        });
    },

    appendPanel: function() {
        if(this.cfg.appendTo) {
            this.panel.appendTo(PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo));
        }
        else {
            this.panel.appendTo(document.body);
        }
    },

    bindEvents: function() {
        var $this = this,
        hideNS = 'mousedown.' + this.id,
        resizeNS = 'resize.' + this.id;

        //Events for checkboxes
        this.bindCheckboxHover(this.checkboxes);
        this.checkboxes.on('click.selectCheckboxMenu', function() {
            $this.toggleItem($(this));
        });

        //Toggler
        this.bindCheckboxHover(this.togglerBox);
        this.togglerBox.on('click.selectCheckboxMenu', function() {
            var el = $(this);
            if(el.hasClass('ui-state-active')) {
                $this.uncheckAll();
                el.addClass('ui-state-hover');
            }
            else {
                $this.checkAll();
                el.removeClass('ui-state-hover');
            }
        });

        //filter
        if(this.cfg.filter) {
            this.setupFilterMatcher();

            PrimeFaces.skinInput(this.filterInput);

            this.filterInput.on('keyup.selectCheckboxMenu', function() {
                $this.filter($(this).val());
            });
        }

        //Closer
        this.closer.on('mouseenter.selectCheckboxMenu', function(){
            $(this).addClass('ui-state-hover');
        }).on('mouseleave.selectCheckboxMenu', function() {
            $(this).removeClass('ui-state-hover');
        }).on('click.selectCheckboxMenu', function(e) {
            $this.hide(true);

            e.preventDefault();
        });

        //Labels
        this.labels.on('click.selectCheckboxMenu', function() {
            var checkbox = $(this).prev().children('.ui-chkbox-box');
            $this.toggleItem(checkbox);
            checkbox.removeClass('ui-state-hover');
            PrimeFaces.clearSelection();
        });

        //Events to show/hide the panel
        this.triggers.on('mouseover.selectCheckboxMenu', function() {
            if(!$this.disabled&&!$this.triggers.hasClass('ui-state-focus')) {
                $this.triggers.addClass('ui-state-hover');
            }
        }).on('mouseout.selectCheckboxMenu', function() {
            if(!$this.disabled) {
                $this.triggers.removeClass('ui-state-hover');
            }
        }).on('mousedown.selectCheckboxMenu', function(e) {
            if(!$this.disabled) {
                if($this.panel.is(":hidden")) {
                    $this.show();
                }
                else {
                    $this.hide(true);
                }
            }
        }).on('click.selectCheckboxMenu', function(e) {
            $this.keyboardTarget.trigger('focus');
            e.preventDefault();
        });
        
        this.bindKeyEvents();

        //hide overlay when outside is clicked
        $(document.body).off(hideNS).on(hideNS, function (e) {
            if($this.panel.is(':hidden')) {
                return;
            }

            //do nothing on trigger mousedown
            var target = $(e.target);
            if($this.triggers.is(target)||$this.triggers.has(target).length > 0) {
                return;
            }

            //hide the panel and remove focus from label
            var offset = $this.panel.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + $this.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.panel.height()) {

                $this.hide(true);
            }
        });

        //Realign overlay on resize
        $(window).off(resizeNS).on(resizeNS, function() {
            if($this.panel.is(':visible')) {
                $this.alignPanel();
            }
        });

        //Client Behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors);
        }
    },
    
    bindKeyEvents: function() {
        var $this = this;

        this.keyboardTarget.on('focus.selectCheckboxMenu', function() {
            $this.jq.addClass('ui-state-focus');
            $this.menuIcon.addClass('ui-state-focus');
        }).on('blur.selectCheckboxMenu', function() {
            $this.jq.removeClass('ui-state-focus');
            $this.menuIcon.removeClass('ui-state-focus');
        }).on('keydown.selectCheckboxMenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;
    
            switch(key) {
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                    if($this.panel.is(":hidden"))
                        $this.show();
                    else
                        $this.hide(true);

                    e.preventDefault();
                break;
                
                case keyCode.TAB:
                    if($this.panel.is(':visible')) {
                       if(!$this.cfg.showHeader) {
                            $this.itemContainer.children('li:not(.ui-state-disabled):first').find('div.ui-helper-hidden-accessible > input').trigger('focus');
                        }
                        else {
                            $this.toggler.find('> div.ui-helper-hidden-accessible > input').trigger('focus'); 
                        }
                        e.preventDefault();
                    }

                break;
                
            };
        });
        
        this.closer.on('focus.selectCheckboxMenu', function(e) {
            $this.closer.addClass('ui-state-focus');
        })
        .on('blur.selectCheckboxMenu', function(e) {
            $this.closer.removeClass('ui-state-focus');
        })
        .on('keydown.selectCheckboxMenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;
            
            if(key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                $this.hide(true);
                e.preventDefault();
            }
        });
        
        var togglerCheckboxInput = this.toggler.find('> div.ui-helper-hidden-accessible > input');
        this.bindCheckboxKeyEvents(togglerCheckboxInput);
        togglerCheckboxInput.on('keyup.selectCheckboxMenu', function(e) {
                    if(e.which === $.ui.keyCode.SPACE) {
                        var input = $(this);
                
                        if(input.prop('checked'))
                            $this.uncheckAll();
                        else                      
                            $this.checkAll();

                        e.preventDefault();
                    }
                });
        
        var itemKeyInputs = this.itemContainer.find('> li > div.ui-chkbox > div.ui-helper-hidden-accessible > input');
        this.bindCheckboxKeyEvents(itemKeyInputs);
        itemKeyInputs.on('keyup.selectCheckboxMenu', function(e) {
                    if(e.which === $.ui.keyCode.SPACE) {
                        var input = $(this),
                        box = input.parent().next();

                        if(input.prop('checked'))
                            $this.uncheck(box, true);
                        else                      
                            $this.check(box, true);

                        e.preventDefault();
                    }
                });
    },

    bindCheckboxHover: function(item) {       
        item.on('mouseenter.selectCheckboxMenu', function() {
            var item = $(this);
            if(!item.hasClass('ui-state-active')&&!item.hasClass('ui-state-disabled')) {
                item.addClass('ui-state-hover');
            }
        }).on('mouseleave.selectCheckboxMenu', function() {
            $(this).removeClass('ui-state-hover');
        });
    },

    filter: function(value) {
        var filterValue = this.cfg.caseSensitive ? $.trim(value) : $.trim(value).toLowerCase();

        if(filterValue === '') {
            this.itemContainer.children('li.ui-selectcheckboxmenu-item').filter(':hidden').show();
        }
        else {
            for(var i = 0; i < this.labels.length; i++) {
                var labelElement = this.labels.eq(i),
                item = labelElement.parent(),
                itemLabel = this.cfg.caseSensitive ? labelElement.text() : labelElement.text().toLowerCase();

                if(this.filterMatcher(itemLabel, filterValue)) {
                    item.show();
                }
                else {
                    item.hide();
                }
            }
        }

        if(this.cfg.scrollHeight) {
            if(this.itemContainer.height() < this.cfg.initialHeight) {
                this.itemContainerWrapper.css('height', 'auto');
            }
            else {
                this.itemContainerWrapper.height(this.cfg.initialHeight);
            }
        }

        this.updateToggler();
    },

    setupFilterMatcher: function() {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode||'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            ,'contains': this.containsFilter
            ,'endsWith': this.endsWithFilter
            ,'custom': this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    },

    startsWithFilter: function(value, filter) {
        return value.indexOf(filter) === 0;
    },

    containsFilter: function(value, filter) {
        return value.indexOf(filter) !== -1;
    },

    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    checkAll: function() {
        var visibleItems = this.itemContainer.children('li.ui-selectcheckboxmenu-item').filter(':visible'),
        $this = this;

        visibleItems.each(function() {
            $this.inputs.eq($(this).index()).prop('checked', true);
            $this.check($(this).children('.ui-chkbox').children('.ui-chkbox-box'));
        });

        this.check(this.togglerBox);
        
        if(!this.togglerBox.hasClass('ui-state-disabled')) {
            this.togglerBox.prev().children('input').trigger('focus.selectCheckboxMenu');
            this.togglerBox.addClass('ui-state-active');    
        }
        
        this.fireToggleSelectEvent(true);
    },

    uncheckAll: function() {
        var visibleItems = this.itemContainer.children('li.ui-selectcheckboxmenu-item').filter(':visible'),
        $this = this;

        visibleItems.each(function() {
            $this.inputs.eq($(this).index()).prop('checked', false);
            $this.uncheck($(this).children('.ui-chkbox').children('.ui-chkbox-box'));
        });

        this.uncheck(this.togglerBox);
        
        if(!this.togglerBox.hasClass('ui-state-disabled')) {
            this.togglerBox.prev().children('input').trigger('focus.selectCheckboxMenu');
        }

        this.fireToggleSelectEvent(false);
    },

    fireToggleSelectEvent: function(checked) {
        if(this.cfg.behaviors) {
            var toggleSelectBehavior = this.cfg.behaviors['toggleSelect'];

            if(toggleSelectBehavior) {
                var ext = {
                    params: [{name: this.id + '_checked', value: checked}]
                }

                toggleSelectBehavior.call(this, ext);
            }
        }
    },

    check: function(checkbox, updateInput) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            var checkedInput = checkbox.prev().children('input');
            
            checkedInput.prop('checked', true);
            if(updateInput) {
                checkedInput.trigger('focus.selectCheckboxMenu');
            }

            checkbox.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
            checkbox.closest('li.ui-selectcheckboxmenu-item').removeClass('ui-selectcheckboxmenu-unchecked').addClass('ui-selectcheckboxmenu-checked');

            if(updateInput) {
                var input = this.inputs.eq(checkbox.parents('li:first').index());
                input.prop('checked', true).change();

                this.updateToggler();
            }
        }
    },

    uncheck: function(checkbox, updateInput) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            var uncheckedInput = checkbox.prev().children('input');
            checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
            checkbox.closest('li.ui-selectcheckboxmenu-item').addClass('ui-selectcheckboxmenu-unchecked').removeClass('ui-selectcheckboxmenu-checked');
            uncheckedInput.prop('checked', false);

            if(updateInput) {
                var input = this.inputs.eq(checkbox.parents('li:first').index());
                input.prop('checked', false).change();
                uncheckedInput.trigger('focus.selectCheckboxMenu');
                this.updateToggler();
            }
        }
    },

    show: function() {
        this.alignPanel();

        this.panel.show();

        this.postShow();
    },

    hide: function(animate) {
        var $this = this;

        if(animate) {
            this.panel.fadeOut('fast', function() {
                $this.postHide();
            });
        }

        else {
            this.panel.hide();
            this.postHide();
        }
    },

    postShow: function() {
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },

    postHide: function() {
        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    },

    alignPanel: function() {
        var fixedPosition = this.panel.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null,
        panelStyle = this.panel.attr('style');

        this.panel.css({
                'left':'',
                'top':'',
                'z-index': ++PrimeFaces.zindex
        });

        if(this.panel.parent().attr('id') === this.id) {
            this.panel.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.panel.position({
                                my: 'left top'
                                ,at: 'left bottom'
                                ,of: this.jq
                                ,offset : positionOffset
                            });
        }

        if(!this.widthAligned && (this.panel.width() < this.jq.width()) && (!panelStyle||panelStyle.toLowerCase().indexOf('width') === -1)) {
            this.panel.width(this.jq.width());
            this.widthAligned = true;
        }
    },

    toggleItem: function(checkbox) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            if(checkbox.hasClass('ui-state-active')) {
                this.uncheck(checkbox, true);
                checkbox.addClass('ui-state-hover');
            }
            else {
                this.check(checkbox, true);
                checkbox.removeClass('ui-state-hover');
            }
        }
    },

    updateToggler: function() {
        var visibleItems = this.itemContainer.children('li.ui-selectcheckboxmenu-item:visible');
        
        if(visibleItems.length && visibleItems.filter('.ui-selectcheckboxmenu-unchecked').length === 0) {
            this.check(this.togglerBox);
        }
        else {
            this.uncheck(this.togglerBox);
        }
    },
    
    bindCheckboxKeyEvents: function(items) {
        var $this = this;
        
        items.on('focus.selectCheckboxMenu', function(e) {
            var input = $(this),
            box = input.parent().next();

            if(input.prop('checked')) {
                box.removeClass('ui-state-active');
            }

            box.addClass('ui-state-focus');
            
            PrimeFaces.scrollInView($this.itemContainerWrapper, box);
        })
        .on('blur.selectCheckboxMenu', function(e) {
            var input = $(this),
            box = input.parent().next();

            if(input.prop('checked')) {
                box.addClass('ui-state-active');
            }

            box.removeClass('ui-state-focus');
        })
        .on('keydown.selectCheckboxMenu', function(e) {
            if(e.which === $.ui.keyCode.SPACE) {
                e.preventDefault();
            }
        });
    }

});

/**
 * PrimeFaces InputMask Widget
 */
PrimeFaces.widget.InputMask = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.mask) {
            this.jq.mask(this.cfg.mask, this.cfg);
        }

        //Visuals
        PrimeFaces.skinInput(this.jq);
    },

    setValue: function(value) {
        this.jq.val(value);
        this.jq.unmask().mask(this.cfg.mask, this.cfg);
    },

    getValue: function() {
        return this.jq.val();
    }

});

/**
 * PrimeFaces Password
 */
PrimeFaces.widget.Password = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        if(!this.jq.is(':disabled')) {
            if(this.cfg.feedback) {
                this.setupFeedback();
            }

            PrimeFaces.skinInput(this.jq);
        }
    },

    setupFeedback: function() {
        var _self = this;

        //remove previous panel if any
        var oldPanel = $(this.jqId + '_panel');
        if(oldPanel.length == 1) {
            oldPanel.remove();
        }

        //config
        this.cfg.promptLabel = this.cfg.promptLabel||'Please enter a password';
        this.cfg.weakLabel = this.cfg.weakLabel||'Weak';
        this.cfg.goodLabel = this.cfg.goodLabel||'Medium';
        this.cfg.strongLabel = this.cfg.strongLabel||'Strong';

        var panelStyle = this.cfg.inline ? 'ui-password-panel-inline' : 'ui-password-panel-overlay';

        //create panel element
        var panelMarkup = '<div id="' + this.id + '_panel" class="ui-password-panel ui-widget ui-state-highlight ui-corner-all ui-helper-hidden ' + panelStyle + '">';
        panelMarkup += '<div class="ui-password-meter" style="background-position:0pt 0pt">&nbsp;</div>';
        panelMarkup += '<div class="ui-password-info">' + this.cfg.promptLabel + '</div>';
        panelMarkup += '</div>';

        this.panel = $(panelMarkup).insertAfter(this.jq);
        this.meter = this.panel.children('div.ui-password-meter');
        this.infoText = this.panel.children('div.ui-password-info');

        if(!this.cfg.inline) {
            this.panel.addClass('ui-shadow');
        }

        //events
        this.jq.focus(function() {
            _self.show();
        })
        .blur(function() {
            _self.hide();
        })
        .keyup(function() {
            var value = _self.jq.val(),
            label = null,
            meterPos = null;

            if(value.length == 0) {
                label = _self.cfg.promptLabel;
                meterPos = '0px 0px';
            }
            else {
                var score = _self.testStrength(_self.jq.val());

                if(score < 30) {
                    label = _self.cfg.weakLabel;
                    meterPos = '0px -10px';
                }
                else if(score >= 30 && score < 80) {
                    label = _self.cfg.goodLabel;
                    meterPos = '0px -20px';
                }
                else if(score >= 80) {
                    label = _self.cfg.strongLabel;
                    meterPos = '0px -30px';
                }
            }

            //update meter and info text
            _self.meter.css('background-position', meterPos);
            _self.infoText.text(label);
        });

        //overlay setting
        if(!this.cfg.inline) {
            this.panel.appendTo('body');

            //Hide overlay on resize
            var resizeNS = 'resize.' + this.id;
            $(window).unbind(resizeNS).bind(resizeNS, function() {
                if(_self.panel.is(':visible')) {
                    _self.align();
                }
            });
        }
    },

    testStrength: function(str) {
        var grade = 0,
        val = 0,
        _self = this;

        val = str.match('[0-9]');
        grade += _self.normalize(val ? val.length : 1/4, 1) * 25;

        val = str.match('[a-zA-Z]');
        grade += _self.normalize(val ? val.length : 1/2, 3) * 10;

        val = str.match('[!@#$%^&*?_~.,;=]');
        grade += _self.normalize(val ? val.length : 1/6, 1) * 35;

        val = str.match('[A-Z]');
        grade += _self.normalize(val ? val.length : 1/6, 1) * 30;

        grade *= str.length / 8;

        return grade > 100 ? 100 : grade;
    },

    normalize: function(x, y) {
        var diff = x - y;

        if(diff <= 0) {
            return x / y;
        }
        else {
            return 1 + 0.5 * (x / (x + y/4));
        }
    },

    align: function() {
        this.panel.css({
            left:'',
            top:'',
            'z-index': ++PrimeFaces.zindex
        })
        .position({
            my: 'left top',
            at: 'right top',
            of: this.jq
        });
    },

    show: function() {
        if(!this.cfg.inline) {
            this.align();

            this.panel.fadeIn();
        }
        else {
            this.panel.slideDown();
        }
    },

    hide: function() {
        if(this.cfg.inline)
            this.panel.slideUp();
        else
            this.panel.fadeOut();
    }

});

/**
 * PrimeFaces DefaultCommand Widget
 */
PrimeFaces.widget.DefaultCommand = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));
        this.scope = this.cfg.scope ? $(PrimeFaces.escapeClientId(this.cfg.scope)) : null;
        var $this = this;

        // container support - e.g. splitButton
        if (this.jqTarget.is(':not(:button):not(:input):not(a)')) {
        	this.jqTarget = this.jqTarget.find('button,a').filter(':visible').first();
        }

        //attach keypress listener to parent form
        this.jqTarget.closest('form').off('keydown.' + this.id).on('keydown.' + this.id, function(e) {
           var keyCode = $.ui.keyCode;
           if(e.which == keyCode.ENTER || e.which == keyCode.NUMPAD_ENTER) {
                //do not proceed if event target is not in this scope or target is a textarea,button or link
                if(($this.scope && $this.scope.find(e.target).length == 0)||$(e.target).is('textarea,button,input[type="submit"],a')) {
                    return true;
                }

               $this.jqTarget.click();
               e.preventDefault();
           }
        });

        this.removeScriptElement(this.id);
    }
});

/*
 * PrimeFaces SplitButton Widget
 */
PrimeFaces.widget.SplitButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.button = $(this.jqId + '_button');
        this.menuButton = $(this.jqId + '_menuButton');
        this.menuId = this.jqId + "_menu";
        this.menu = $(this.menuId);
        this.menuitems = this.menu.find('.ui-menuitem:not(.ui-state-disabled)');
        this.cfg.disabled = this.button.is(':disabled');

        if(!this.cfg.disabled) {
            this.bindEvents();
            this.appendPanel();
        }

        //pfs metadata
        this.button.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.menuButton.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    refresh: function(cfg) {
        this.menu.remove();

        this.init(cfg);
    },
    
    destroy: function() {
        this._super();
        
        this.menu.remove();
    },

    bindEvents: function() {
        var $this = this;

        PrimeFaces.skinButton(this.button).skinButton(this.menuButton);

        //mark button and descandants of button as a trigger for a primefaces overlay
        this.button.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        //toggle menu
        this.menuButton.click(function() {
            if($this.menu.is(':hidden')) {
                $this.show();
            }
            else {
                $this.hide();
            }
        });

        //menuitem visuals
        this.menuitems.mouseover(function(e) {
            var menuitem = $(this),
            menuitemLink = menuitem.children('.ui-menuitem-link');

            if(!menuitemLink.hasClass('ui-state-disabled')) {
                menuitem.addClass('ui-state-hover');
            }
        }).mouseout(function(e) {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            $this.hide();
        });

        var hideNS = 'mousedown.' + this.id;
        $(document.body).off(hideNS).on(hideNS, function (e) {
            //do nothing if hidden already
            if($this.menu.is(":hidden")) {
                return;
            }

            //do nothing if mouse is on button
            var target = $(e.target);
            if(target.is($this.button)||$this.button.has(target).length > 0) {
                return;
            }

            //hide overlay if mouse is outside of overlay except button
            var offset = $this.menu.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + $this.menu.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.menu.height()) {

                $this.button.removeClass('ui-state-focus ui-state-hover');
                $this.hide();
            }
        });

        var resizeNS = 'resize.' + this.id;
        $(window).off(resizeNS).on(resizeNS, function() {
            if($this.menu.is(':visible')) {
                $this.alignPanel();
            }
        });
    },

    appendPanel: function() {
        var container = this.cfg.appendTo ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo): $(document.body);

        if(!container.is(this.jq)) {
            container.children(this.menuId).remove();
            this.menu.appendTo(container);
        }
    },

    show: function() {
        this.alignPanel();

        this.menuButton.focus();

        this.menu.show();
    },

    hide: function() {
        this.menuButton.removeClass('ui-state-focus');

        this.menu.fadeOut('fast');
    },

    alignPanel: function() {
        this.menu.css({left:'', top:'','z-index': ++PrimeFaces.zindex});

        if(this.menu.parent().is(this.jq)) {
            this.menu.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.menu.position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.button
            });
        }
    }

});

/*
 * PrimeFaces ThemeSwitcher Widget
 */
PrimeFaces.widget.ThemeSwitcher = PrimeFaces.widget.SelectOneMenu.extend({

    init: function(cfg) {
        this._super(cfg);

        var $this = this;
        this.input.on('change', function() {
            PrimeFaces.changeTheme($this.getSelectedValue());
        });
    }
});

/*
 * PrimeFaces MultiSelectListbox Widget
 */
PrimeFaces.widget.MultiSelectListbox = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
       this._super(cfg);

       this.root = this.jq.children('div.ui-multiselectlistbox-listcontainer');
       this.items = this.jq.find('li.ui-multiselectlistbox-item');
       this.input = $(this.jqId + '_input');
       this.cfg.disabled = this.jq.hasClass('ui-state-disabled');

       if(!this.cfg.disabled) {
           this.bindEvents();
       }

       var value = this.input.val();
       if(value !== '') {
           this.preselect(value);
       }
    },

    bindEvents: function() {
       var $this = this;

       this.items.on('mouseover.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('ui-state-highlight'))
               $(this).addClass('ui-state-hover');
       })
       .on('mouseout.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('ui-state-highlight'))
               $(this).removeClass('ui-state-hover');
       })
       .on('click.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('ui-state-highlight'))
               $this.showOptionGroup(item);
       })
    },

    unbindEvents: function() {
       this.items.off('mouseover.multiSelectListbox mouseout.multiSelectListbox click.multiSelectListbox');
    },

    showOptionGroup: function(item) {
       item.addClass('ui-state-highlight').removeClass('ui-state-hover').siblings().filter('.ui-state-highlight').removeClass('ui-state-highlight');
       item.closest('.ui-multiselectlistbox-listcontainer').nextAll().remove();
       this.input.val(item.attr('data-value'));

       var childItemsContainer = item.children('ul');

       if(childItemsContainer.length) {
           var groupContainer = $('<div class="ui-multiselectlistbox-listcontainer" style="display:none"></div>');
           childItemsContainer.clone(true).appendTo(groupContainer).addClass('ui-multiselectlistbox-list ui-inputfield ui-widget-content').removeClass('ui-helper-hidden');
           
           if(this.cfg.showHeaders) {
               groupContainer.prepend('<div class="ui-multiselectlistbox-header ui-widget-header ui-corner-top">' + item.children('span').text() + '</div>')
                       .children('.ui-multiselectlistbox-list').addClass('ui-corner-bottom');
           } else {
               groupContainer.children().addClass('ui-corner-all');
           } 
            
           this.jq.append(groupContainer);

           if(this.cfg.effect)
               groupContainer.show(this.cfg.effect);
           else
               groupContainer.show();
       }
    },

    enable: function() {
       if(this.cfg.disabled) {
           this.cfg.disabled = false;
           this.jq.removeClass('ui-state-disabled');
           this.bindEvents();
       }

    },

    disable: function() {
       if(!this.cfg.disabled) {
           this.cfg.disabled = true;
           this.jq.addClass('ui-state-disabled');
           this.unbindEvents();
           this.root.nextAll().remove();
       }
    },

    preselect: function(value) {
        var $this = this,
        item = this.items.filter('[data-value="' + value + '"]');

        if(item.length === 0) {
            return;
        }

        var ancestors = item.parentsUntil('.ui-multiselectlistbox-list'),
        selectedIndexMap = [];

        for(var i = (ancestors.length - 1); i >= 0; i--) {
            var ancestor = ancestors.eq(i);

            if(ancestor.is('li')) {
                selectedIndexMap.push(ancestor.index());
            }
            else if(ancestor.is('ul')) {
                var groupContainer = $('<div class="ui-multiselectlistbox-listcontainer ui-inputfield ui-widget-content ui-corner-all" style="display:none"></div>');
                ancestor.clone(true).appendTo(groupContainer).addClass('ui-multiselectlistbox-list').removeClass('ui-helper-hidden');

                $this.jq.append(groupContainer);
            }
        }

        //highlight item
        var lists = this.jq.children('div.ui-multiselectlistbox-listcontainer'),
        clonedItem = lists.find(' > ul.ui-multiselectlistbox-list > li.ui-multiselectlistbox-item').filter('[data-value="' + value + '"]');
        clonedItem.addClass('ui-state-highlight');

        //highlight ancestors
        for(var i = 0; i < selectedIndexMap.length; i++) {
            lists.eq(i).find('> .ui-multiselectlistbox-list > li.ui-multiselectlistbox-item').eq(selectedIndexMap[i]).addClass('ui-state-highlight');
        }

        $this.jq.children('div.ui-multiselectlistbox-listcontainer:hidden').show();
    }
});
/**
 * PrimeFaces Growl Widget
 */
PrimeFaces.widget.Growl = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id
        this.jqId = PrimeFaces.escapeClientId(this.id);

        this.render();
        
        this.removeScriptElement(this.id);
    },
    
    //Override
    refresh: function(cfg) {
    	this.cfg = cfg;
        this.show(cfg.msgs);
        
        this.removeScriptElement(this.id);
    },
    
    show: function(msgs) {
        var _self = this;
        
        this.jq.css('z-index', ++PrimeFaces.zindex);

        //clear previous messages
        this.removeAll();

        $.each(msgs, function(index, msg) {
            _self.renderMessage(msg);
        }); 
    },
    
    removeAll: function() {
        this.jq.children('div.ui-growl-item-container').remove();
    },
    
    render: function() {
        //create container
        this.jq = $('<div id="' + this.id + '_container" class="ui-growl ui-widget"></div>');
        this.jq.appendTo($(document.body));

        //render messages
        this.show(this.cfg.msgs);
    },
    
    renderMessage: function(msg) {
        var markup = '<div class="ui-growl-item-container ui-state-highlight ui-corner-all ui-helper-hidden ui-shadow" aria-live="polite">';
        markup += '<div class="ui-growl-item">';
        markup += '<div class="ui-growl-icon-close ui-icon ui-icon-closethick" style="display:none"></div>';
        markup += '<span class="ui-growl-image ui-growl-image-' + msg.severity + '" />';
        markup += '<div class="ui-growl-message">';
        markup += '<span class="ui-growl-title"></span>';
        markup += '<p></p>';
        markup += '</div><div style="clear: both;"></div></div></div>';

        var message = $(markup),
        summaryEL = message.find('span.ui-growl-title'),
        detailEL = summaryEL.next();
        
        if(this.cfg.escape) {
            summaryEL.text(msg.summary);
            detailEL.text(msg.detail);
        }
        else {
            summaryEL.html(msg.summary);
            detailEL.html(msg.detail);
        }

        this.bindEvents(message);

        message.appendTo(this.jq).fadeIn();
    },
    
    bindEvents: function(message) {
        var _self = this,
        sticky = this.cfg.sticky;

        message.mouseover(function() {
            var msg = $(this);

            //visuals
            if(!msg.is(':animated')) {
                msg.find('div.ui-growl-icon-close:first').show();
            }
        })
        .mouseout(function() {        
            //visuals
            $(this).find('div.ui-growl-icon-close:first').hide();
        });

        //remove message on click of close icon
        message.find('div.ui-growl-icon-close').click(function() {
            _self.removeMessage(message);

            //clear timeout if removed manually
            if(!sticky) {
                clearTimeout(message.data('timeout'));
            }
        });

        //hide the message after given time if not sticky
        if(!sticky) {
            this.setRemovalTimeout(message);
        }
    },
    
    removeMessage: function(message) {
        message.fadeTo('normal', 0, function() {
            message.slideUp('normal', 'easeInOutCirc', function() {
                message.remove();
            });
        });
    },
    
    setRemovalTimeout: function(message) {
        var _self = this;

        var timeout = setTimeout(function() {
            _self.removeMessage(message);
        }, this.cfg.life);

        message.data('timeout', timeout);
    }
});
/**
 * PrimeFaces Inplace Widget
 */
PrimeFaces.widget.Inplace = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.display = $(this.jqId + '_display');
        this.content = $(this.jqId + '_content');
        this.cfg.formId = this.jq.parents('form:first').attr('id');

        var $this = this;

        if(!this.cfg.disabled) {

            if(this.cfg.toggleable) {
                this.display.bind(this.cfg.event, function(){
                    $this.show();
                });

                this.display.mouseover(function(){
                    $(this).toggleClass("ui-state-highlight");
                }).mouseout(function(){
                    $(this).toggleClass("ui-state-highlight");
                });
            }
            else {
                this.display.css('cursor', 'default');
            }

            if(this.cfg.editor) {
                this.cfg.formId = $(this.jqId).parents('form:first').attr('id');

                this.editor = $(this.jqId + '_editor');

                var saveButton = this.editor.children('.ui-inplace-save'),
                cancelButton = this.editor.children('.ui-inplace-cancel');

                PrimeFaces.skinButton(saveButton).skinButton(cancelButton);

                saveButton.click(function(e) {$this.save(e)});
                cancelButton.click(function(e) {$this.cancel(e)});
            }
        }
    },
    
    show: function() {    
        this.toggle(this.content, this.display);
    },
    
    hide: function() {
        this.toggle(this.display, this.content);
    },
    
    toggle: function(elToShow, elToHide) {
        var $this = this;

        if(this.cfg.effect === 'fade') {
            elToHide.fadeOut(this.cfg.effectSpeed,
                function() {
                    elToShow.fadeIn($this.cfg.effectSpeed);
                    $this.postShow();
                });
        }
        else if(this.cfg.effect === 'slide') {
            elToHide.slideUp(this.cfg.effectSpeed,
                function() {
                    elToShow.slideDown($this.cfg.effectSpeed);
                    $this.postShow();
            });
        }
        else if(this.cfg.effect === 'none') {
            elToHide.hide();
            elToShow.show();
            $this.postShow();
        }
    },
    
    postShow: function() {
        this.content.find('input:text,textarea').filter(':visible:enabled:first').focus().select();
        
        PrimeFaces.invokeDeferredRenders(this.id);
    },
    
    getDisplay: function() {
        return this.display;
    },
    
    getContent: function() {
        return this.content;
    },
    
    save: function(e) {
        var options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId
        };

        if(this.hasBehavior('save')) {
            var saveBehavior = this.cfg.behaviors['save'];

            saveBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.AjaxRequest(options); 
        }
    },
    
    cancel: function(e) {
        var options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId
        };

        options.params = [
            {name: this.id + '_cancel', value: true}
        ];

        if(this.hasBehavior('cancel')) {
            var saveBehavior = this.cfg.behaviors['cancel'];

            saveBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.AjaxRequest(options); 
        }
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }

        return false;
    }
    
});
/**
 * PrimeFaces LightBox Widget
 */
PrimeFaces.widget.LightBox = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.links = this.jq.children(':not(.ui-lightbox-inline)');

        this.createPanel();

        if(this.cfg.mode === 'image') {
            this.setupImaging();
        } else if(this.cfg.mode === 'inline') {
            this.setupInline();
        } else if(this.cfg.mode === 'iframe') {
            this.setupIframe();
        }

        this.bindCommonEvents();

        if(this.cfg.visible) {
            this.links.eq(0).click();
        }

        this.panel.data('widget', this);
        this.links.data('primefaces-lightbox-trigger', true).find('*').data('primefaces-lightbox-trigger', true);
    },
    
    refresh: function(cfg) {
        $(PrimeFaces.escapeClientId(cfg.id) + '_panel').remove();
        
        this.init(cfg);
    },
    
    destroy: function() {
        this.panel.remove();
    },
    
    createPanel: function() {
        var dom = '<div id="' + this.id + '_panel" class="ui-lightbox ui-widget ui-helper-hidden ui-corner-all ui-shadow">';
        dom += '<div class="ui-lightbox-content-wrapper">';
        dom += '<a class="ui-state-default ui-lightbox-nav-left ui-corner-right ui-helper-hidden"><span class="ui-icon ui-icon-carat-1-w">go</span></a>';
        dom += '<div class="ui-lightbox-content ui-corner-all"></div>';
        dom += '<a class="ui-state-default ui-lightbox-nav-right ui-corner-left ui-helper-hidden"><span class="ui-icon ui-icon-carat-1-e">go</span></a>';
        dom += '</div>';
        dom += '<div class="ui-lightbox-caption ui-widget-header"><span class="ui-lightbox-caption-text"></span>';
        dom += '<a class="ui-lightbox-close ui-corner-all" href="#"><span class="ui-icon ui-icon-closethick"></span></a><div style="clear:both" /></div>';
        dom += '</div>';
        
        $(document.body).append(dom);
        
        this.panel = $(this.jqId + '_panel');
        this.contentWrapper = this.panel.children('.ui-lightbox-content-wrapper');
        this.content = this.contentWrapper.children('.ui-lightbox-content');
        this.caption = this.panel.children('.ui-lightbox-caption');
        this.captionText = this.caption.children('.ui-lightbox-caption-text');        
        this.closeIcon = this.caption.children('.ui-lightbox-close');
        this.closeIcon.data('primefaces-lightbox-trigger', true).find('*').data('primefaces-lightbox-trigger', true);
    },
    
    setupImaging: function() {
        var _self = this;

        this.content.append('<img class="ui-helper-hidden"></img>');
        this.imageDisplay = this.content.children('img');
        this.navigators = this.contentWrapper.children('a');
        
        this.imageDisplay.load(function() { 
            var image = $(this);
            
            _self.scaleImage(image);

            //coordinates to center overlay
            var leftOffset = (_self.panel.width() - image.width()) / 2,
            topOffset = (_self.panel.height() - image.height()) / 2;
            
            //resize content for new image
            _self.content.removeClass('ui-lightbox-loading').animate({
                width: image.width()
                ,height: image.height()
            },
            500,
            function() {            
                //show image
                image.fadeIn();
                _self.showNavigators();
                _self.caption.slideDown();
            });

            _self.panel.animate({
                left: '+=' + leftOffset
                ,top: '+=' + topOffset
            }, 500);
        });

        this.navigators.mouseover(function() {
            $(this).addClass('ui-state-hover'); 
        })
        .mouseout(function() {
            $(this).removeClass('ui-state-hover'); 
        })
        .click(function(e) {
            var nav = $(this);

            _self.hideNavigators();

            if(nav.hasClass('ui-lightbox-nav-left')) {
                var index = _self.current == 0 ? _self.links.length - 1 : _self.current - 1;

                _self.links.eq(index).trigger('click');
            } 
            else {
                var index = _self.current == _self.links.length - 1 ? 0 : _self.current + 1;

                _self.links.eq(index).trigger('click');
            }

            e.preventDefault(); 
        });

        this.links.click(function(e) {
            var link = $(this);
            
            if(_self.isHidden()) {
                _self.content.addClass('ui-lightbox-loading').width(32).height(32);
                _self.show();
            }
            else {
                _self.imageDisplay.fadeOut(function() {
                    //clear for onload scaling
                    $(this).css({
                        'width': 'auto'
                        ,'height': 'auto'
                    });

                    _self.content.addClass('ui-lightbox-loading');
                });

                _self.caption.slideUp();
            }

            setTimeout(function() {
                _self.imageDisplay.attr('src', link.attr('href'));
                _self.current = link.index();
                
                var title = link.attr('title');
                if(title) {
                    _self.captionText.html(title);
                }
            }, 1000);


            e.preventDefault();
        });
    },
    
    scaleImage: function(image) {
        var win = $(window),
        winWidth = win.width(),
        winHeight = win.height(),
        imageWidth = image.width(),
        imageHeight = image.height(),
        ratio = imageHeight / imageWidth;
        
        if(imageWidth >= winWidth && ratio <= 1){
            imageWidth = winWidth * 0.75;
            imageHeight = imageWidth * ratio;
        } 
        else if(imageHeight >= winHeight){
            imageHeight = winHeight * 0.75;
            imageWidth = imageHeight / ratio;
        }

        image.css({
            'width':imageWidth + 'px'
            ,'height':imageHeight + 'px'
        })
    },
    
    setupInline: function() {
        this.inline = this.jq.children('.ui-lightbox-inline');
        this.inline.appendTo(this.content).show();
        var _self = this;

        this.links.click(function(e) {
            _self.show();

            var title = $(this).attr('title');
            if(title) {
                _self.captionText.html(title);
                _self.caption.slideDown();
            }

            e.preventDefault();
        });
    },
    
    setupIframe: function() {
        var $this = this;
        this.iframeLoaded = false;
        this.cfg.width = this.cfg.width||'640px';
        this.cfg.height = this.cfg.height||'480px';
        
        this.iframe = $('<iframe frameborder="0" style="width:' + this.cfg.width + ';height:' 
                        + this.cfg.height + ';border:0 none; display: block;"></iframe>').appendTo(this.content);
        
        if(this.cfg.iframeTitle) {
            this.iframe.attr('title', this.cfg.iframeTitle);
        }

        this.links.click(function(e) {
            if(!$this.iframeLoaded) {
                $this.content.addClass('ui-lightbox-loading').css({
                    width: $this.cfg.width
                    ,height: $this.cfg.height
                });
                $this.show();
                
                $this.iframe.on('load', function() {
                                $this.iframeLoaded = true;
                                $this.content.removeClass('ui-lightbox-loading');
                            })
                            .attr('src', $this.links.eq(0).attr('href'));
            }
            else {
                $this.show();
            }
            
            var title = $this.links.eq(0).attr('title');
            if(title) {
                $this.captionText.text(title);
                $this.caption.slideDown();
            }
                
            e.preventDefault();
        });
    },
    
    bindCommonEvents: function() {
        var $this = this,
        hideNS = PrimeFaces.env.ios ? 'touchstart.' + this.id: 'click.' + this.id,
        resizeNS = 'resize.' + this.id;
        
        this.closeIcon.mouseover(function() {
            $(this).addClass('ui-state-hover');
        })
        .mouseout(function() {
            $(this).removeClass('ui-state-hover');
        })

        this.closeIcon.click(function(e) {
            $this.hide();
            e.preventDefault();
        });

        //hide when outside is clicked
        $(document.body).off(hideNS).on(hideNS, function (e) {            
            if($this.isHidden()) {
                return;
            }
            
            //do nothing if target is the link
            var target = $(e.target);
            if(target.data('primefaces-lightbox-trigger')) {
                return;
            }

            //hide if mouse is outside of lightbox
            var offset = $this.panel.offset(),
            pageX, pageY;
            
            if(e.originalEvent.touches) {
                pageX = e.originalEvent.touches[0].pageX;
                pageY = e.originalEvent.touches[0].pageY;
            } else {
                pageX = e.pageX;
                pageY = e.pageY;
            }
            
            if(pageX < offset.left ||
                pageX > offset.left + $this.panel.width() ||
                pageY < offset.top ||
                pageY > offset.top + $this.panel.height()) {

                e.preventDefault();
                $this.hide();
            }
        });
        
        //sync window resize
        $(window).off(resizeNS).on(resizeNS, function() {
            if(!$this.isHidden()) {
                $(document.body).children('.ui-widget-overlay').css({
                    'width': $(document).width()
                    ,'height': $(document).height()
                });
            }
        });
    },
    
    show: function() {
        this.center();
        
        this.panel.css('z-index', ++PrimeFaces.zindex).show();
        
        if(!this.isModalActive()) {
            this.enableModality();
        }

        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },
    
    hide: function() {
        this.panel.fadeOut();
        this.disableModality();
        this.caption.hide();

        if(this.cfg.mode == 'image') {
            this.imageDisplay.hide().attr('src', '').removeAttr('style');
            this.hideNavigators();
        }

        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    },
    
    center: function() { 
        var win = $(window),
        left = (win.width() / 2 ) - (this.panel.width() / 2),
        top = (win.height() / 2 ) - (this.panel.height() / 2);

        this.panel.css({
            'left': left,
            'top': top
        });
    },
    
    enableModality: function() {
        $(document.body).append('<div id="' + this.id + '_modal" class="ui-widget-overlay"></div>').
            children(this.jqId + '_modal').css({
                'width': $(document).width()
                ,'height': $(document).height()
                ,'z-index': this.panel.css('z-index') - 1
            });
    },
    
    disableModality: function() {
        $(document.body).children(this.jqId + '_modal').remove();
    },
    
    isModalActive: function() {
        return $(document.body).children(this.jqId + '_modal').length === 1;
    },
    
    showNavigators: function() {
        this.navigators.zIndex(this.imageDisplay.zIndex() + 1).show();
    },
    
    hideNavigators: function() {
        this.navigators.hide();
    },
    
    addOnshowHandler: function(fn) {
        this.onshowHandlers.push(fn);
    },
    
    isHidden: function() {
        return this.panel.is(':hidden');
    },
    
    showURL: function(opt) {
        if(opt.width)
            this.iframe.attr('width', opt.width);
        if(opt.height)
            this.iframe.attr('height', opt.height);
        
        this.iframe.attr('src', opt.src); 
        
        this.captionText.text(opt.title||'');
        this.caption.slideDown();
        
        this.show();
    }
    
});
/**
 * PrimeFaces Menu Widget
 */
PrimeFaces.widget.Menu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        if(this.cfg.overlay) {
            this.initOverlay();
        }
        
        this.keyboardTarget = this.jq.children('.ui-helper-hidden-accessible');
    },
    
    initOverlay: function() {
        var $this = this;
        
        this.trigger = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.trigger);

        //mark trigger and descandants of trigger as a trigger for a primefaces overlay
        this.trigger.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        /*
         * we might have two menus with same ids if an ancestor of a menu is updated,
         * if so remove the previous one and refresh jq
         */
        if(this.jq.length > 1){
            $(document.body).children(this.jqId).remove();
            this.jq = $(this.jqId);
            this.jq.appendTo(document.body);
        }
        else if(this.jq.parent().is(':not(body)')) {
            this.jq.appendTo(document.body);
        }

        this.cfg.pos = {
            my: this.cfg.my
            ,at: this.cfg.at
            ,of: this.trigger
        }

        this.trigger.bind(this.cfg.triggerEvent + '.ui-menu', function(e) {
            var trigger = $(this);
            
            if($this.jq.is(':visible')) {
                $this.hide();
            }
            else {
                $this.show();
                
                if(trigger.is(':button')) {
                    trigger.addClass('ui-state-focus');
                }
                
                e.preventDefault();
            }   
        });

        //hide overlay on document click
        var hideNS = 'mousedown.' + this.id;
        $(document.body).off(hideNS).on(hideNS, function (e) {            
            if($this.jq.is(":hidden")) {
                return;
            }

            //do nothing if mousedown is on trigger
            var target = $(e.target);
            if(target.is($this.trigger.get(0))||$this.trigger.has(target).length > 0) {
                return;
            }

            //hide if mouse is outside of overlay except trigger
            var offset = $this.jq.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + $this.jq.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.jq.height()) {
                
                $this.hide(e);
            }
        });

        //Hide overlay on resize
        var resizeNS = 'resize.' + this.id;
        $(window).off(resizeNS).on(resizeNS, function() {
            if($this.jq.is(':visible')) {
                $this.align();
            }
        });

        //dialog support
        this.setupDialogSupport();
    },
    
    setupDialogSupport: function() {
        var dialog = this.trigger.parents('.ui-dialog:first');

        if(dialog.length == 1) {
            this.jq.css('position', 'fixed');
        }
    },
    
    show: function() {
        this.align();
        this.jq.css('z-index', ++PrimeFaces.zindex).show();
    },
    
    hide: function() {
        this.jq.fadeOut('fast');
        
        if(this.trigger && this.trigger.is(':button')) {
            this.trigger.removeClass('ui-state-focus');
        }
    },
    
    align: function() {
        var fixedPosition = this.jq.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;

        this.cfg.pos.offset = positionOffset;

        this.jq.css({left:'', top:''}).position(this.cfg.pos);
    }
});

/**
 * PrimeFaces TieredMenu Widget
 */
PrimeFaces.widget.TieredMenu = PrimeFaces.widget.Menu.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.toggleEvent = this.cfg.toggleEvent||'hover';
        this.links = this.jq.find('a.ui-menuitem-link:not(.ui-state-disabled)');
        this.rootLinks = this.jq.find('> ul.ui-menu-list > .ui-menuitem > .ui-menuitem-link');
                
        this.bindEvents();
    },
    
    bindEvents: function() {        
        this.bindItemEvents();
        this.bindKeyEvents();
        this.bindDocumentHandler();
    },
    
    bindItemEvents: function() {        
        if(this.cfg.toggleEvent === 'hover')
            this.bindHoverModeEvents();
        else if(this.cfg.toggleEvent === 'click')
            this.bindClickModeEvents();
    },
    
    bindHoverModeEvents: function() {
        var $this = this;
        
        this.links.mouseenter(function() {
            var link = $(this),
            menuitem = link.parent();

            var activeSibling = menuitem.siblings('.ui-menuitem-active');
            if(activeSibling.length === 1) {
                activeSibling.find('li.ui-menuitem-active').each(function() {
                    $this.deactivate($(this));
                });
                $this.deactivate(activeSibling);
            }

            if($this.cfg.autoDisplay||$this.active) {
                if(menuitem.hasClass('ui-menuitem-active'))
                    $this.reactivate(menuitem);
                else
                    $this.activate(menuitem);
            }
            else {
                $this.highlight(menuitem);
            }
        });

        this.rootLinks.click(function(e) {
            var link = $(this),
            menuitem = link.parent(),
            submenu = menuitem.children('ul.ui-menu-child');

            $this.itemClick = true;

            if(submenu.length === 1) {
                if(submenu.is(':visible')) {
                    $this.active = false;
                    $this.deactivate(menuitem);
                }
                else {                     
                    $this.active = true;
                    $this.highlight(menuitem);
                    $this.showSubmenu(menuitem, submenu);
                }
            }
        });

        this.links.filter('.ui-submenu-link').click(function(e) {
            $this.itemClick = true;
            e.preventDefault();
        });

        this.jq.find('ul.ui-menu-list').mouseleave(function(e) {
           if($this.activeitem) {
               $this.deactivate($this.activeitem);
           }

           e.stopPropagation();
        });
    },
    
    bindClickModeEvents: function() {
        var $this = this;
        
        this.links.mouseenter(function() {
            var menuitem = $(this).parent();
            
            if(!menuitem.hasClass('ui-menuitem-active')) {
                menuitem.addClass('ui-menuitem-highlight').children('a.ui-menuitem-link').addClass('ui-state-hover');
            }
        })
        .mouseleave(function() {
            var menuitem = $(this).parent();

            if(!menuitem.hasClass('ui-menuitem-active')) {
                menuitem.removeClass('ui-menuitem-highlight').children('a.ui-menuitem-link').removeClass('ui-state-hover');
            }
        });

        this.links.filter('.ui-submenu-link').on('click.tieredMenu', function(e) {
            var link = $(this),
            menuitem = link.parent(),
            submenu = menuitem.children('ul.ui-menu-child');

            $this.itemClick = true;

            var activeSibling = menuitem.siblings('.ui-menuitem-active');
            if(activeSibling.length) {
                activeSibling.find('li.ui-menuitem-active').each(function() {
                    $this.deactivate($(this));
                });
                $this.deactivate(activeSibling);
            }

            if(submenu.length) {
                if(submenu.is(':visible')) {
                    $this.deactivate(menuitem);
                    menuitem.addClass('ui-menuitem-highlight').children('a.ui-menuitem-link').addClass('ui-state-hover');
                }
                else {
                    menuitem.addClass('ui-menuitem-active').children('a.ui-menuitem-link').removeClass('ui-state-hover').addClass('ui-state-active');
                    $this.showSubmenu(menuitem, submenu);
                }
            }

            e.preventDefault();
        });
    },
            
    bindKeyEvents: function() {
        //not implemented
    },
    
    bindDocumentHandler: function() {
        var $this = this,
        clickNS = 'click.' + this.id;
        
        $(document.body).off(clickNS).on(clickNS, function(e) {
            if($this.itemClick) {
                $this.itemClick = false;
                return;
            }
            
            $this.reset();
        });
    },
    
    deactivate: function(menuitem, animate) {
        this.activeitem = null;
        menuitem.children('a.ui-menuitem-link').removeClass('ui-state-hover ui-state-active');
        menuitem.removeClass('ui-menuitem-active ui-menuitem-highlight');
        
        if(animate)
            menuitem.children('ul.ui-menu-child').fadeOut('fast');
        else
            menuitem.children('ul.ui-menu-child').hide();
    },
    
    activate: function(menuitem) {
        this.highlight(menuitem);

        var submenu = menuitem.children('ul.ui-menu-child');
        if(submenu.length == 1) {
            this.showSubmenu(menuitem, submenu);
        }
    },
    
    reactivate: function(menuitem) {
        this.activeitem = menuitem;
        var submenu = menuitem.children('ul.ui-menu-child'),
        activeChilditem = submenu.children('li.ui-menuitem-active:first'),
        _self = this;
        
        if(activeChilditem.length == 1) {
            _self.deactivate(activeChilditem);
        }
    },
    
    highlight: function(menuitem) {
        this.activeitem = menuitem;
        menuitem.children('a.ui-menuitem-link').addClass('ui-state-hover');
        menuitem.addClass('ui-menuitem-active');
    },
    
    showSubmenu: function(menuitem, submenu) {
        var pos ={
            my: 'left top',
            at: 'right top',
            of: menuitem,
            collision: 'flipfit'
        };

        submenu.css('z-index', ++PrimeFaces.zindex)
            .show()
            .position(pos);
    },
            
    reset: function() {
        var $this = this;
        this.active = false;

        this.jq.find('li.ui-menuitem-active').each(function() {
            $this.deactivate($(this), true);
        });
    }
    
});

/**
 * PrimeFaces Menubar Widget
 */
PrimeFaces.widget.Menubar = PrimeFaces.widget.TieredMenu.extend({
    
    showSubmenu: function(menuitem, submenu) {
        var pos = null;
        
        if(menuitem.parent().hasClass('ui-menu-child')) {
            pos = {
                my: 'left top',
                at: 'right top',
                of: menuitem,
                collision: 'flipfit'
            };
        }
        else {
            pos = {
                my: 'left top',
                at: 'left bottom',
                of: menuitem,
                collision: 'flipfit'
            };
        }
        
        submenu.css('z-index', ++PrimeFaces.zindex)
                .show()
                .position(pos);
    },
          
    //@Override
    bindKeyEvents: function() {
        var $this = this;

        this.keyboardTarget.on('focus.menubar', function(e) {
            $this.highlight($this.links.eq(0).parent());
        })
        .on('blur.menubar', function() {
            $this.reset();
        })
        .on('keydown.menu', function(e) {
            var currentitem = $this.activeitem;
            if(!currentitem) {
                return;
            }
            
            var isRootLink = !currentitem.closest('ul').hasClass('ui-menu-child'),
            keyCode = $.ui.keyCode;
            
            switch(e.which) {
                    case keyCode.LEFT:
                        if(isRootLink) {
                            var prevItem = currentitem.prevAll('.ui-menuitem:not(.ui-menubar-options):first');
                            if(prevItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(prevItem);
                            }
                            
                            e.preventDefault();
                        }
                        else {
                            if(currentitem.hasClass('ui-menu-parent') && currentitem.children('.ui-menu-child').is(':visible')) {
                                $this.deactivate(currentitem);
                                $this.highlight(currentitem);
                            }
                            else {
                                var parentItem = currentitem.parent().parent();
                                $this.deactivate(currentitem);
                                $this.deactivate(parentItem);
                                $this.highlight(parentItem);
                            }
                        }
                    break;
                    
                    case keyCode.RIGHT:
                        if(isRootLink) {
                            var nextItem = currentitem.nextAll('.ui-menuitem:not(.ui-menubar-options):first');
                            if(nextItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(nextItem);
                            }

                            e.preventDefault();
                        }
                        else {
                            if(currentitem.hasClass('ui-menu-parent')) {
                                var submenu = currentitem.children('.ui-menu-child');
                                
                                if(submenu.is(':visible'))
                                    $this.highlight(submenu.children('.ui-menuitem:first'));
                                else
                                    $this.activate(currentitem);
                            }
                        }
                    break;
                    
                    case keyCode.UP:
                        if(!isRootLink) {         
                            var prevItem = currentitem.prev('.ui-menuitem');
                            if(prevItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(prevItem);
                            }                   
                        }
                        
                        e.preventDefault();
                    break;
                    
                    case keyCode.DOWN:
                        if(isRootLink) {
                            var submenu = currentitem.children('ul.ui-menu-child');
                            if(submenu.is(':visible'))
                                $this.highlight(submenu.children('.ui-menuitem:first'));
                            else
                                $this.activate(currentitem);                        
                        }
                        else {
                            var nextItem = currentitem.next('.ui-menuitem');
                            if(nextItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(nextItem);
                            }
                        }
                        
                        e.preventDefault();
                    break;
                    
                    case keyCode.ENTER:
                    case keyCode.NUMPAD_ENTER:
                        var currentLink = currentitem.children('.ui-menuitem-link');
                        currentLink.trigger('click');
                        $this.jq.blur();
                        var href = currentLink.attr('href');
                        if(href && href !== '#') {
                            window.location.href = href;
                        }
                        
                        e.preventDefault();
                    break;
                    
            }        
        });
    }
    
});

/**
 * PrimeFaces SlideMenu Widget
 */
PrimeFaces.widget.SlideMenu = PrimeFaces.widget.Menu.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        //elements
        this.submenus = this.jq.find('ul.ui-menu-list');
        this.wrapper = this.jq.children('div.ui-slidemenu-wrapper');
        this.content = this.wrapper.children('div.ui-slidemenu-content');
        this.rootList = this.content.children('ul.ui-menu-list');
        this.links = this.jq.find('a.ui-menuitem-link:not(.ui-state-disabled)');
        this.backward = this.wrapper.children('div.ui-slidemenu-backward');
        this.rendered = false;
                
        //config
        this.stack = [];
        this.jqWidth = this.jq.width();
        
        if(!this.jq.hasClass('ui-menu-dynamic')) {
            
            if(this.jq.is(':not(:visible)')) {
                var hiddenParent = this.jq.closest('.ui-hidden-container'),
                hiddenParentWidgetVar = hiddenParent.data('widget'),
                $this = this;

                if(hiddenParentWidgetVar) {
                    var hiddenParentWidget = PF(hiddenParentWidgetVar);
                    
                    if(hiddenParentWidget) {
                        hiddenParentWidget.addOnshowHandler(function() {
                            return $this.render();
                        });
                    }
                }
            }
            else {
                this.render();
            }
        }
                
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        this.links.mouseenter(function() {
           $(this).addClass('ui-state-hover'); 
        })
        .mouseleave(function() {
           $(this).removeClass('ui-state-hover'); 
        })
        .click(function(e) {
            var link = $(this),
            submenu = link.next();
           
            if(submenu.length) {
               $this.forward(submenu);
               e.preventDefault();
            }
        });
        
        this.backward.click(function() {
            $this.back();
        });
    },
    
    forward: function(submenu) {
        var _self = this;
        
        this.push(submenu);
        
        var rootLeft = -1 * (this.depth() * this.jqWidth);
        
        submenu.show().css({
            left: this.jqWidth
        });
               
        this.rootList.animate({
            left: rootLeft
        }, 500, 'easeInOutCirc', function() {
            if(_self.backward.is(':hidden')) {
                _self.backward.fadeIn('fast');
            }
        });
    },
    
    back: function() {
        var _self = this,
        last = this.pop(),
        depth = this.depth();
            
        var rootLeft = -1 * (depth * this.jqWidth);

        this.rootList.animate({
            left: rootLeft
        }, 500, 'easeInOutCirc', function() {
            if(last !== null) {
                last.hide();
            }
            
            if(depth == 0) {
                _self.backward.fadeOut('fast');
            }
        });
    },
    
    push: function(submenu) {
        this.stack.push(submenu);
    },
    
    pop: function() {
        return this.stack.length !== 0 ? this.stack.pop() : null;
    },
    
    last: function() {
        return this.stack[this.stack.length - 1];
    },
    
    depth: function() {
        return this.stack.length;
    },
    
    render: function() {
        this.submenus.width(this.jq.width());
        this.wrapper.height(this.rootList.outerHeight(true) + this.backward.outerHeight(true));
        this.content.height(this.rootList.outerHeight(true));
        this.rendered = true;
    },
    
    show: function() {                
        this.align();
        this.jq.css('z-index', ++PrimeFaces.zindex).show();
        
        if(!this.rendered) {
            this.render();
        }
    }
});

/**
 * PrimeFaces PlainMenu Widget
 */
PrimeFaces.widget.PlainMenu = PrimeFaces.widget.Menu.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.menuitemLinks = this.jq.find('.ui-menuitem-link:not(.ui-state-disabled)');
        
        //events
        this.bindEvents();
        
        if(this.cfg.toggleable) {
            this.collapsedIds = [];
            this.stateKey = 'menu-' + this.id;
            this.restoreState();
        }
    },
            
    bindEvents: function() {  
        var $this = this;
        
        this.menuitemLinks.mouseenter(function(e) {
            if($this.jq.is(':focus')) {
                $this.jq.blur();
            }
            
            $(this).addClass('ui-state-hover');
        })
        .mouseleave(function(e) {
            $(this).removeClass('ui-state-hover');
        });

        if(this.cfg.overlay) {
            this.menuitemLinks.click(function() {
                $this.hide();
            });  
        }
        
        if(this.cfg.toggleable) {
            this.jq.find('> .ui-menu-list > .ui-widget-header').on('mouseover.menu', function() {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseout.menu', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.menu', function(e) {
                var header = $(this);

                if(header.find('> h3 > .ui-icon').hasClass('ui-icon-triangle-1-s'))
                    $this.collapseSubmenu(header, true);
                else
                    $this.expandSubmenu(header, true);
                
                PrimeFaces.clearSelection();
                e.preventDefault();
            });
        }
        
        this.keyboardTarget.on('focus.menu', function() {
            $this.menuitemLinks.eq(0).addClass('ui-state-hover');
        })
        .on('blur.menu', function() {
            $this.menuitemLinks.filter('.ui-state-hover').removeClass('ui-state-hover');
        })
        .on('keydown.menu', function(e) {
            var currentLink = $this.menuitemLinks.filter('.ui-state-hover'),
            keyCode = $.ui.keyCode;
            
            switch(e.which) {
                    case keyCode.UP:
                        var prevItem = currentLink.parent().prevAll('.ui-menuitem:first');
                        if(prevItem.length) {
                            currentLink.removeClass('ui-state-hover');
                            prevItem.children('.ui-menuitem-link').addClass('ui-state-hover');
                        }

                        e.preventDefault();
                    break;
                    
                    case keyCode.DOWN:
                        var nextItem = currentLink.parent().nextAll('.ui-menuitem:first');
                        if(nextItem.length) {
                            currentLink.removeClass('ui-state-hover');
                            nextItem.children('.ui-menuitem-link').addClass('ui-state-hover');
                        }
                        
                        e.preventDefault();
                    break;
                    
                    case keyCode.ENTER:
                    case keyCode.NUMPAD_ENTER:
                        currentLink.trigger('click');
                        $this.jq.blur();
                        var href = currentLink.attr('href');
                        if(href && href !== '#') {
                            window.location.href = href;
                        }
                        
                        e.preventDefault();
                    break;
                    
            }        
        });
    },
            
    collapseSubmenu: function(header, stateful) {
        var items = header.nextUntil('li.ui-widget-header');
        
        header.attr('aria-expanded', false)
                .find('> h3 > .ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');

        items.hide();

        if(stateful) {
            this.collapsedIds.push(header.attr('id'));
            this.saveState();
        }
    },

    expandSubmenu: function(header, stateful) {
        var items = header.nextUntil('li.ui-widget-header');

        header.attr('aria-expanded', false)
                .find('> h3 > .ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');

        items.show();

        if(stateful) {
            var id = header.attr('id');        
            this.collapsedIds = $.grep(this.collapsedIds, function(value) {
                return (value !== id);
            });
            this.saveState();
        }
    },
    
    saveState: function() {        
        PrimeFaces.setCookie(this.stateKey, this.collapsedIds.join(','));
    },
    
    restoreState: function() {
        var collapsedIdsAsString = PrimeFaces.getCookie(this.stateKey);

        if(collapsedIdsAsString) {
            this.collapsedIds = collapsedIdsAsString.split(',');
            
            for(var i = 0 ; i < this.collapsedIds.length; i++) {
                this.collapseSubmenu($(PrimeFaces.escapeClientId(this.collapsedIds[i])), false);
            }
        }
    },
    
    clearState: function() {
        PrimeFaces.setCookie(this.stateKey, null);
    }
    
});
            
/*
 * PrimeFaces MenuButton Widget
 */
PrimeFaces.widget.MenuButton = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.menuId = this.jqId + '_menu';
        this.button = this.jq.children('button');
        this.menu = this.jq.children('.ui-menu');
        this.menuitems = this.jq.find('.ui-menuitem');
        this.cfg.disabled = this.button.is(':disabled');

        if(!this.cfg.disabled) {
            this.bindEvents();
            this.appendPanel();
        }
    },
        
    bindEvents: function() {  
        var $this = this;

        //button visuals
        this.button.mouseover(function(){
            if(!$this.button.hasClass('ui-state-focus')) {
                $this.button.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            if(!$this.button.hasClass('ui-state-focus')) {
                $this.button.removeClass('ui-state-hover ui-state-active');
            }
        }).mousedown(function() {
            $(this).removeClass('ui-state-focus ui-state-hover').addClass('ui-state-active');
        }).mouseup(function() {
            var el = $(this);
            el.removeClass('ui-state-active')

            if($this.menu.is(':visible')) {
                el.addClass('ui-state-hover');
                $this.hide();
            } 
            else {
                el.addClass('ui-state-focus');
                $this.show();
            }
        }).focus(function() {
            $(this).addClass('ui-state-focus');
        }).blur(function() {
            $(this).removeClass('ui-state-focus');
        });

        //mark button and descandants of button as a trigger for a primefaces overlay
        this.button.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        //menuitem visuals
        this.menuitems.mouseover(function(e) {
            var element = $(this);
            if(!element.hasClass('ui-state-disabled')) {
                element.addClass('ui-state-hover');
            }
        }).mouseout(function(e) {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            $this.button.removeClass('ui-state-focus');
            $this.hide();
        });

        /**
        * handler for document mousedown to hide the overlay
        **/
        var hideNS = 'mousedown.' + this.id;
        $(document.body).off(hideNS).on(hideNS, function (e) {
            //do nothing if hidden already
            if($this.menu.is(":hidden") || $this.cfg.disabled) {
                return;
            }

            //do nothing if mouse is on button
            var target = $(e.target);
            if(target.is($this.button)||$this.button.has(target).length > 0) {
                return;
            }

            //hide overlay if mouse is outside of overlay except button
            var offset = $this.menu.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + $this.menu.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.menu.height()) {

                $this.button.removeClass('ui-state-focus ui-state-hover');
                $this.hide();
            }
        });

        //Realign overlay on window resize
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.menu.is(':visible')) {
                $this.alignPanel();
            }
        });

        //aria
        this.button.attr('role', 'button').attr('aria-disabled', this.button.is(':disabled'));
    },
    
    appendPanel: function() {
        var container = this.cfg.appendTo ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo): $(document.body);
        
        if(!container.is(this.jq)) {
            container.children(this.menuId).remove();
            this.menu.appendTo(container);
        }
    },
        
    show: function() {
        this.alignPanel();

        this.menu.show();
    },
    
    hide: function() {
        this.menu.fadeOut('fast');
    },
    
    alignPanel: function() {
        this.menu.css({left:'', top:'','z-index': ++PrimeFaces.zindex});
        
        if(this.menu.parent().is(this.jq)) {
            this.menu.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.menu.position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.button
            });
        }
    }
    
});

/**
 * PrimeFaces ContextMenu Widget
 */
PrimeFaces.widget.ContextMenu = PrimeFaces.widget.TieredMenu.extend({
    
    init: function(cfg) {
        cfg.autoDisplay = true;
        this._super(cfg);
        this.cfg.selectionMode = this.cfg.selectionMode||'multiple';
        
        var _self = this,
        documentTarget = (this.cfg.target === undefined);

        //event
        this.cfg.event = this.cfg.event||'contextmenu';

        //target
        this.jqTargetId = documentTarget ? document : PrimeFaces.escapeClientId(this.cfg.target);
        this.jqTarget = $(this.jqTargetId);
        
        //append to body
        if(!this.jq.parent().is(document.body)) {
            this.jq.appendTo('body');
        }
        
        //attach contextmenu
        if(documentTarget) {
            $(document).off('contextmenu.ui-contextmenu').on('contextmenu.ui-contextmenu', function(e) {
                _self.show(e);
            });
        }
        else {
            if(this.cfg.type === 'DataTable') {
                this.bindDataTable();
            }
            else if(this.cfg.type === 'TreeTable') {
                this.bindTreeTable();
            }
            else if(this.cfg.type === 'Tree') {
                this.bindTree();
            }
            else {                
                var event = this.cfg.event + '.ui-contextmenu';
                
                $(document).off(event, this.jqTargetId).on(event, this.jqTargetId, null, function(e) {
                    _self.show(e);
                });
            }
            
        }
    },
    
    bindDataTable: function() {
        var rowSelector = this.jqTargetId + ' tbody.ui-datatable-data > tr.ui-widget-content:not(.ui-datatable-empty-message)',
        event = this.cfg.event + '.datatable',
        $this = this;
        
        $(document).off(event, rowSelector)
                    .on(event, rowSelector, null, function(e) {
                        var widget = PrimeFaces.widgets[$this.cfg.targetWidgetVar],
                        row = $(this);
                
                        if(widget.cfg.selectionMode && row.hasClass('ui-datatable-selectable')) {
                            widget.onRowRightClick(e, this, $this.cfg.selectionMode);

                            $this.show(e);
                        }
                        else if(widget.cfg.editMode === 'cell') {
                            var target = $(e.target),
                            cell = target.is('td.ui-editable-column') ? target : target.parents('td.ui-editable-column:first');
                            
                            if(widget.contextMenuCell) {
                                widget.contextMenuCell.removeClass('ui-state-highlight');
                            }
                            
                            widget.contextMenuClick = true;
                            widget.contextMenuCell = cell;
                            widget.contextMenuCell.addClass('ui-state-highlight');
                            
                            $this.show(e);
                        }
                    });
    },
    
    bindTreeTable: function() {
        var rowSelector = this.jqTargetId + ' .ui-treetable-data > ' + (this.cfg.nodeType ? 'tr.ui-treetable-selectable-node.' + this.cfg.nodeType : 'tr.ui-treetable-selectable-node'),
        event = this.cfg.event + '.treetable',
        _self = this;
        
        $(document).off(event, rowSelector)
                    .on(event, rowSelector, null, function(e) {
                    	PrimeFaces.widgets[_self.cfg.targetWidgetVar].onRowRightClick(e, $(this));
                        _self.show(e);
                    });
    },
    
    bindTree: function() {
        var nodeContentSelector = this.jqTargetId + ' .ui-tree-selectable',
        nodeEvent = this.cfg.nodeType ? this.cfg.event + '.treenode.' + this.cfg.nodeType : this.cfg.event + '.treenode',
        containerEvent = this.cfg.event + '.tree',
        $this = this;
                
        $(document).off(nodeEvent, nodeContentSelector)
                    .on(nodeEvent, nodeContentSelector, null, function(e) {
                        var nodeContent = $(this);
                        
                        if($this.cfg.nodeType === undefined || nodeContent.parent().data('nodetype') === $this.cfg.nodeType) {
                        	PrimeFaces.widgets[$this.cfg.targetWidgetVar].nodeRightClick(e, nodeContent);
                            $this.show(e);
                        }
                    });
                                    
        $(document).off(containerEvent, this.jqTargetId)
                    .on(containerEvent, this.jqTargetId, null, function(e) {
                if(PrimeFaces.widgets[$this.cfg.targetWidgetVar].isEmpty()) {
                    $this.show(e);
                }
        });
    },
    
    refresh: function(cfg) {
        var jqId = PrimeFaces.escapeClientId(cfg.id),
        instances = $(jqId);
        
        if(instances.length > 1) {
            $(document.body).children(jqId).remove();
        }

        this.init(cfg);
    },
    
    bindItemEvents: function() {
        this._super();
        
        var _self = this;
        
        //hide menu on item click
        this.links.bind('click', function() {
            _self.hide();
        });
    },
    
    bindDocumentHandler: function() {
        var $this = this,
        clickNS = 'click.' + this.id;
        
        //hide overlay when document is clicked
        $(document.body).off(clickNS).on(clickNS, function(e) {
            if($this.jq.is(":hidden")) {
                return;
            }
                        
            $this.hide();
        });
    },
    
    show: function(e) { 
        if(this.cfg.targetFilter && $(e.target).is(':not(' + this.cfg.targetFilter + ')')) {
            return;
        }
        
        //hide other contextmenus if any
        $(document.body).children('.ui-contextmenu:visible').hide();

        var win = $(window),
        left = e.pageX,
        top = e.pageY,
        width = this.jq.outerWidth(),
        height = this.jq.outerHeight();

        //collision detection for window boundaries
        if((left + width) > (win.width())+ win.scrollLeft()) {
            left = left - width;
        }
        if((top + height ) > (win.height() + win.scrollTop())) {
            top = top - height;
        }
        
        if(this.cfg.beforeShow) {
            var retVal = this.cfg.beforeShow.call(this, e);
            if(retVal === false) {
                return;
            }
        }

        this.jq.css({
            'left': left,
            'top': top,
            'z-index': ++PrimeFaces.zindex
        }).show();

        e.preventDefault();
        e.stopPropagation();
    },
    
    hide: function() {
        var _self = this;
        
        //hide submenus
        this.jq.find('li.ui-menuitem-active').each(function() {
            _self.deactivate($(this), true);
        });
        
        this.jq.fadeOut('fast');
    },
    
    isVisible: function() {
        return this.jq.is(':visible');
    },
    
    getTarget: function() {
        return this.jqTarget;
    }

});

/**
 * PrimeFaces MegaMenu Widget
 */
PrimeFaces.widget.MegaMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.vertical = this.jq.hasClass('ui-megamenu-vertical');
        this.rootList = this.jq.children('ul.ui-menu-list');
        this.rootLinks = this.rootList.find('> li.ui-menuitem > a.ui-menuitem-link:not(.ui-state-disabled)');                  
        this.subLinks = this.jq.find('.ui-menu-child a.ui-menuitem-link:not(.ui-state-disabled)');
        
        if(this.cfg.activeIndex !== undefined) {
            this.rootLinks.eq(this.cfg.activeIndex).addClass('ui-state-active');
        }
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
  
        this.rootLinks.mouseenter(function(e) {
            var link = $(this),
            menuitem = link.parent();
            
            var current = menuitem.siblings('.ui-menuitem-active');
            if(current.length > 0) {
                $this.deactivate(current, false);
            }
            
            if($this.cfg.autoDisplay||$this.active) {
                $this.activate(menuitem);
            }
            else {
                $this.highlight(menuitem);
            }
            
        });
        
        if(this.cfg.autoDisplay === false) {
            this.rootLinks.data('primefaces-megamenu', this.id).find('*').data('primefaces-megamenu', this.id)
            
            this.rootLinks.click(function(e) {
                var link = $(this),
                menuitem = link.parent(),
                submenu = link.next();

                if(submenu.length === 1) {
                    if(submenu.is(':visible')) {
                        $this.active = false;
                        $this.deactivate(menuitem, true);
                    }
                    else {                                        
                        $this.active = true;
                        $this.activate(menuitem);
                    }
                }
                
                e.preventDefault();
            });
        }
        else {
            this.rootLinks.filter('.ui-submenu-link').click(function(e) {
                e.preventDefault();
            });
        }

        this.subLinks.mouseenter(function() {
            $(this).addClass('ui-state-hover');
        })
        .mouseleave(function() {
            $(this).removeClass('ui-state-hover');
        });
        
        this.rootList.mouseleave(function(e) {
            var activeitem = $this.rootList.children('.ui-menuitem-active');
            if(activeitem.length === 1) {
                $this.deactivate(activeitem, false);
            }
        });
        
        this.rootList.find('> li.ui-menuitem > ul.ui-menu-child').mouseleave(function(e) {            
            e.stopPropagation();
        });
        
        $(document.body).click(function(e) {
            var target = $(e.target);
            if(target.data('primefaces-megamenu') === $this.id) {
                return;
            }
            
            $this.active = false;
            $this.deactivate($this.rootList.children('li.ui-menuitem-active'), true);
        });
    },
    
    deactivate: function(menuitem, animate) {
        var link = menuitem.children('a.ui-menuitem-link'),
        submenu = link.next();
        
        menuitem.removeClass('ui-menuitem-active');
        link.removeClass('ui-state-hover');
        
        if(submenu.length > 0) {
            if(animate)
                submenu.fadeOut('fast');
            else
                submenu.hide();
        }
    },
    
    highlight: function(menuitem) {
        var link = menuitem.children('a.ui-menuitem-link');

        menuitem.addClass('ui-menuitem-active');
        link.addClass('ui-state-hover');
    },
    
    activate: function(menuitem) {
        var submenu = menuitem.children('.ui-menu-child'),
        $this = this;
        
        $this.highlight(menuitem);
        
        if(submenu.length > 0) {
            $this.showSubmenu(menuitem, submenu);
        }
    },
    
    showSubmenu: function(menuitem, submenu) {
        var pos = null;
        
        if(this.cfg.vertical) {
            pos = {
                my: 'left top',
                at: 'right top',
                of: menuitem,
                collision: 'flipfit'
            };
        }
        else {
            pos = {
                my: 'left top',
                at: 'left bottom',
                of: menuitem,
                collision: 'flipfit'
            };
        }

        submenu.css('z-index', ++PrimeFaces.zindex)
                .show()
                .position(pos);
    }
    
});
/**
 * PrimeFaces PanelMenu Widget
 */
PrimeFaces.widget.PanelMenu = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.headers = this.jq.find('> .ui-panelmenu-panel > h3.ui-panelmenu-header:not(.ui-state-disabled)');
        this.menuitemLinks = this.jq.find('.ui-menuitem-link:not(.ui-state-disabled)');
        this.treeLinks = this.jq.find('.ui-menu-parent > .ui-menuitem-link:not(.ui-state-disabled)');
        this.bindEvents();
        
        if(this.cfg.stateful) {
            this.stateKey = 'panelMenu-' + this.id;
        }
        
        this.restoreState();
    },

    bindEvents: function() {
        var _self = this;

        this.headers.mouseover(function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')) {
                element.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')) {
                element.removeClass('ui-state-hover');
            }
        }).click(function(e) {
            var header = $(this);

            if(header.hasClass('ui-state-active'))
                _self.collapseRootSubmenu($(this));
            else
                _self.expandRootSubmenu($(this), false);

            e.preventDefault();
        });

        this.menuitemLinks.mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        });

        this.treeLinks.click(function(e) {
            var link = $(this),
            submenu = link.parent(),
            submenuList = link.next();

            if(submenuList.is(':visible'))
                _self.collapseTreeItem(submenu);
            else
                _self.expandTreeItem(submenu, false);

            e.preventDefault();
        });
    },

    collapseRootSubmenu: function(header) {
        var panel = header.next();

        header.attr('aria-expanded', false).removeClass('ui-state-active ui-corner-top').addClass('ui-state-hover ui-corner-all')
                            .children('.ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');

        panel.attr('aria-hidden', true).slideUp('normal', 'easeInOutCirc');
        
        this.removeAsExpanded(panel);
    },

    expandRootSubmenu: function(header, restoring) {
        var panel = header.next();

        header.attr('aria-expanded', false).addClass('ui-state-active ui-corner-top').removeClass('ui-state-hover ui-corner-all')
                .children('.ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');

        if(restoring) {
            panel.attr('aria-hidden', false).show();
        }
        else {
            panel.attr('aria-hidden', false).slideDown('normal', 'easeInOutCirc');
            
            this.addAsExpanded(panel);
        }
    },

    expandTreeItem: function(submenu, restoring) {
        submenu.find('> .ui-menuitem-link > .ui-panelmenu-icon').addClass('ui-icon-triangle-1-s');
        submenu.children('.ui-menu-list').show();
        
        if(!restoring) {
            this.addAsExpanded(submenu);
        }
    },

    collapseTreeItem: function(submenu) {
        submenu.find('> .ui-menuitem-link > .ui-panelmenu-icon').removeClass('ui-icon-triangle-1-s');
        submenu.children('.ui-menu-list').hide();
        
        this.removeAsExpanded(submenu);
    },
    
    saveState: function() {
        if(this.cfg.stateful) {
            var expandedNodeIds = this.expandedNodes.join(',');

            PrimeFaces.setCookie(this.stateKey, expandedNodeIds, {path:'/'});
        }
    },
    
    restoreState: function() {
        var expandedNodeIds = null; 
        
        if(this.cfg.stateful) {
            expandedNodeIds = PrimeFaces.getCookie(this.stateKey);
        }

        if(expandedNodeIds) {
            this.collapseAll();
            this.expandedNodes = expandedNodeIds.split(',');
            
            for(var i = 0 ; i < this.expandedNodes.length; i++) {
                var element = $(PrimeFaces.escapeClientId(this.expandedNodes[i]));
                if(element.is('div.ui-panelmenu-content'))
                    this.expandRootSubmenu(element.prev(), true);
                else if(element.is('li.ui-menu-parent'))
                    this.expandTreeItem(element, true);
            }
        }
        else {
            this.expandedNodes = [];
            var activeHeaders = this.headers.filter('.ui-state-active'),
            activeTreeSubmenus = this.jq.find('.ui-menu-parent > .ui-menu-list:not(.ui-helper-hidden)');
    
            for(var i = 0; i < activeHeaders.length; i++) {
                this.expandedNodes.push(activeHeaders.eq(i).next().attr('id'));
            }
            
            for(var i = 0; i < activeTreeSubmenus.length; i++) {
                this.expandedNodes.push(activeTreeSubmenus.eq(i).parent().attr('id'));
            }
        }
    },
    
    removeAsExpanded: function(element) {
        var id = element.attr('id');
        
        this.expandedNodes = $.grep(this.expandedNodes, function(value) {
            return value != id;
        });
        
        this.saveState();
    },

    addAsExpanded: function(element) {
        this.expandedNodes.push(element.attr('id'));

        this.saveState();
    },
    
    clearState: function() {
        if(this.cfg.stateful) {
            PrimeFaces.deleteCookie(this.stateKey, {path:'/'});
        }
    },
    
    collapseAll: function() {
        this.headers.filter('.ui-state-active').each(function() {
            var header = $(this);
            header.removeClass('ui-state-active').children('.ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');
            header.next().addClass('ui-helper-hidden');
        });
        
        this.jq.find('.ui-menu-parent > .ui-menu-list:not(.ui-helper-hidden)').each(function() {
            $(this).addClass('ui-helper-hidden').prev().children('.ui-panelmenu-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
        });
    }

});

/**
 * PrimeFaces TabMenu Widget
 */
PrimeFaces.widget.TabMenu = PrimeFaces.widget.Menu.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.items = this.jq.find('> .ui-tabmenu-nav > li:not(.ui-state-disabled)');

        this.bindEvents();
    },
    
    bindEvents: function() {
        this.items.on('mouseover.tabmenu', function(e) {
                    var element = $(this);
                    if(!element.hasClass('ui-state-active')) {
                        element.addClass('ui-state-hover');
                    }
                })
                .on('mouseout.tabmenu', function(e) {
                    $(this).removeClass('ui-state-hover');
                });
    }
});
/**
 * PrimeFaces Message Widget
 */
PrimeFaces.widget.Message = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        var text = this.jq.children('.ui-message-error-detail').text();
        
        if(text) {
           $(PrimeFaces.escapeClientId(this.cfg.target)).data('tooltip', text);
        }
    }
});
/**
 * PrimeFaces NotificationBar Widget
 */
PrimeFaces.widget.NotificationBar = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        var _self = this;
	
        //relocate
        this.jq.css(this.cfg.position, '0').appendTo($('body'));

        //display initially
        if(this.cfg.autoDisplay) {
            $(this.jq).css('display','block')
        }

        //bind events
        this.jq.children('.ui-notificationbar-close').click(function() {
            _self.hide();
        });
    },
    
    show: function() {
        if(this.cfg.effect === 'slide')
            $(this.jq).slideDown(this.cfg.effect);
        else if(this.cfg.effect === 'fade')
            $(this.jq).fadeIn(this.cfg.effect);
        else if(this.cfg.effect === 'none')
            $(this.jq).show();
    },
    
    hide: function() {
        if(this.cfg.effect === 'slide')
            $(this.jq).slideUp(this.cfg.effect);
        else if(this.cfg.effect === 'fade')
            $(this.jq).fadeOut(this.cfg.effect);
        else if(this.cfg.effect === 'none')
            $(this.jq).hide();
    },
    
    isVisible: function() {
        return this.jq.is(':visible');
    },

    toggle: function() {
        if(this.isVisible())
            this.hide();
        else
            this.show();
    }
    
});
/**
 * PrimeFaces Panel Widget
 */
PrimeFaces.widget.Panel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.header = this.jq.children('div.ui-panel-titlebar');
        this.title = this.header.children('span.ui-panel-title');
        this.content = $(this.jqId + '_content');
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        if(this.cfg.toggleable) {
            this.bindToggler();
        }

        if(this.cfg.closable) {
            this.bindCloser();
        }
        
        if(this.cfg.hasMenu) {
            $(this.jqId + '_menu').on('click.panel', function(e) {
                e.preventDefault();
            });
        }
        
        //visuals for action items
        this.header.find('.ui-panel-titlebar-icon').on('mouseover.panel',function() {
            $(this).addClass('ui-state-hover');
        }).on('mouseout.panel',function() {
            $(this).removeClass('ui-state-hover');
        }).on('click.panel', function(e) {
            e.preventDefault();
        });
    },
            
    toggle: function() {
        if(this.cfg.collapsed) {
            this.expand();
            PrimeFaces.invokeDeferredRenders(this.id);
        }
        else {
            this.collapse();
        }
    },
    
    expand: function() {
        this.toggleState(false, 'ui-icon-plusthick', 'ui-icon-minusthick');
        
        if(this.cfg.toggleOrientation === 'vertical')
            this.slideDown();
        else if(this.cfg.toggleOrientation === 'horizontal')
            this.slideRight();    
    },
    
    collapse: function() {
        this.toggleState(true, 'ui-icon-minusthick', 'ui-icon-plusthick');
        
        if(this.cfg.toggleOrientation === 'vertical')
            this.slideUp();
        else if(this.cfg.toggleOrientation === 'horizontal')
            this.slideLeft();
    },
    
    slideUp: function() {        
        this.content.slideUp(this.cfg.toggleSpeed, 'easeInOutCirc');
    },
    
    slideDown: function() {        
        this.content.slideDown(this.cfg.toggleSpeed, 'easeInOutCirc');
    },
    
    slideLeft: function() {
        var _self = this;
        
        this.originalWidth = this.jq.width();
                
        this.title.hide();
        this.toggler.hide();
        this.content.hide();

        this.jq.animate({
            width: '42px'
        }, this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            _self.toggler.show();
            _self.jq.addClass('ui-panel-collapsed-h');
        });
    },
    
    slideRight: function() {
        var _self = this,
        expandWidth = this.originalWidth||'100%';
        
        this.toggler.hide();
        
        this.jq.animate({
            width: expandWidth
        }, this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            _self.jq.removeClass('ui-panel-collapsed-h');
            _self.title.show();
            _self.toggler.show();
        
            _self.content.css({
                'visibility': 'visible'
                ,'display': 'block'
                ,'height': 'auto'
            });
        });
    },
    
    toggleState: function(collapsed, removeIcon, addIcon) {
        this.toggler.children('span.ui-icon').removeClass(removeIcon).addClass(addIcon);
        this.cfg.collapsed = collapsed;
        this.toggleStateHolder.val(collapsed);
        
        this.fireToggleEvent();
    },
    
    fireToggleEvent: function() {
        if(this.cfg.behaviors) {
            var toggleBehavior = this.cfg.behaviors['toggle'];
            
            if(toggleBehavior) {
                toggleBehavior.call(this);
            }
        }
    },
    
    close: function() {
        if(this.visibleStateHolder) {
            this.visibleStateHolder.val(false);
        }
        
        var $this = this;
        this.jq.fadeOut(this.cfg.closeSpeed,
            function(e) {
                if($this.cfg.behaviors) {
                    var closeBehavior = $this.cfg.behaviors['close'];
                    if(closeBehavior) {
                        closeBehavior.call($this);
                    }
                }
            }
        );
    },
    
    show: function() {
        var $this = this;
        $(this.jqId).fadeIn(this.cfg.closeSpeed, function() {
            PrimeFaces.invokeDeferredRenders($this.id);
        });

        this.visibleStateHolder.val(true);
    },
    
    bindToggler: function() {
        var _self = this;
        
        this.toggler = $(this.jqId + '_toggler');
        this.toggleStateHolder = $(this.jqId + '_collapsed');

        this.toggler.click(function() {_self.toggle();});
    },
    
    bindCloser: function() {
        var $this = this;
        
        this.closer = $(this.jqId + '_closer');
        this.visibleStateHolder = $(this.jqId + "_visible");

        this.closer.click(function(e) {
            $this.close();
            e.preventDefault();
        });
    }

});
/**
 * PrimeFaces Poll Widget
 */
PrimeFaces.widget.Poll = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.active = false;

        if(this.cfg.autoStart) {
            this.start();
        }
    },
    
    refresh: function(cfg) {
        if(this.isActive()) {
            this.stop();
        }
        
        this.init(cfg);
    },

    start: function() {
        this.timer = setInterval(this.cfg.fn, (this.cfg.frequency * 1000));
        this.active = true;
    },

    stop: function() {
        clearInterval(this.timer);
        this.active = false;
    },

    isActive: function() {
        return this.active;
    }
});
/**
 * PrimeFaces OrderList Widget
 */
PrimeFaces.widget.OrderList = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.list = this.jq.find('.ui-orderlist-list'),
        this.items = this.list.children('.ui-orderlist-item');
        this.input = $(this.jqId + '_values');
        this.cfg.effect = this.cfg.effect||'fade';
        this.cfg.disabled = this.jq.hasClass('ui-state-disabled');
        var $this = this;

        if(!this.cfg.disabled) {
            this.generateItems();

            this.setupButtons();

            this.bindEvents();

            //Enable dnd
            this.list.sortable({
                revert: 1,
                start: function(event, ui) {
                    PrimeFaces.clearSelection();
                }
                ,update: function(event, ui) {
                    $this.onDragDrop(event, ui);
                }
            });
        }
    },
    
    generateItems: function() {
        var $this = this;

        this.list.children('.ui-orderlist-item').each(function() {
            var item = $(this),
            itemValue = item.data('item-value');

            $this.input.append('<option value="' + itemValue + '" selected="selected">' + itemValue + '</option>');
        });
    },
    
    bindEvents: function() {
        var $this = this;
        
        this.items.on('mouseover.orderList', function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight'))
                $(this).addClass('ui-state-hover');
        })
        .on('mouseout.orderList', function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight'))
                $(this).removeClass('ui-state-hover');
        })
        .on('mousedown.orderList', function(e) {
            var element = $(this),
            metaKey = (e.metaKey||e.ctrlKey);

            if(!metaKey) {
                element.removeClass('ui-state-hover').addClass('ui-state-highlight')
                .siblings('.ui-state-highlight').removeClass('ui-state-highlight');
        
                $this.fireItemSelectEvent(element, e);
            }
            else {
                if(element.hasClass('ui-state-highlight')) {
                    element.removeClass('ui-state-highlight');
                    $this.fireItemUnselectEvent(element);
                }
                else {
                    element.removeClass('ui-state-hover').addClass('ui-state-highlight');
                    $this.fireItemSelectEvent(element, e);
                }
            }
        });
    },
    
    setupButtons: function() {
        var $this = this;

        PrimeFaces.skinButton(this.jq.find('.ui-button'));

        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-up').click(function() {$this.moveUp($this.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-top').click(function() {$this.moveTop($this.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-down').click(function() {$this.moveDown($this.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-bottom').click(function() {$this.moveBottom($this.sourceList);});
    },
    
    onDragDrop: function(event, ui) {
        ui.item.removeClass('ui-state-highlight');
        this.saveState();
        this.fireReorderEvent();
    },
    
    saveState: function() {
        this.input.children().remove();

        this.generateItems();
    },
    
    moveUp: function() {
        var $this = this,
        selectedItems = this.items.filter('.ui-state-highlight'),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

        selectedItems.each(function() {
            var item = $(this);

            if(!item.is(':first-child')) {
                item.hide($this.cfg.effect, {}, 'fast', function() {
                    item.insertBefore(item.prev()).show($this.cfg.effect, {}, 'fast', function() {
                        movedItemsCount++;
                        
                        if(itemsToMoveCount === movedItemsCount) {
                            $this.saveState();
                            $this.fireReorderEvent();
                        }
                    });
                });
            }
            else {
                itemsToMoveCount--;
            }
        });
    },
    
    moveTop: function() {
        var $this = this,
        selectedItems = this.items.filter('.ui-state-highlight'),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

        selectedItems.each(function() {
            var item = $(this);

            if(!item.is(':first-child')) {
                item.hide($this.cfg.effect, {}, 'fast', function() {
                    item.prependTo(item.parent()).show($this.cfg.effect, {}, 'fast', function(){
                        movedItemsCount++;
                        
                        if(itemsToMoveCount === movedItemsCount) {
                            $this.saveState();
                            $this.fireReorderEvent();
                        }
                    });
                });
            }
            else {
                itemsToMoveCount--;
            }
        });
    },
    
    moveDown: function() {
        var $this = this,
        selectedItems = $(this.items.filter('.ui-state-highlight').get().reverse()),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

        selectedItems.each(function() {
            var item = $(this);

            if(!item.is(':last-child')) {                
                item.hide($this.cfg.effect, {}, 'fast', function() {
                    item.insertAfter(item.next()).show($this.cfg.effect, {}, 'fast', function() {
                        movedItemsCount++;
                        
                        if(itemsToMoveCount === movedItemsCount) {
                            $this.saveState();
                            $this.fireReorderEvent();
                        }
                    });
                });
            }
            else {
                itemsToMoveCount--;
            }
        });
    },
    
    moveBottom: function() {
        var $this = this,
        selectedItems = this.items.filter('.ui-state-highlight'),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

        selectedItems.each(function() {
            var item = $(this);

            if(!item.is(':last-child')) {
                item.hide($this.cfg.effect, {}, 'fast', function() {
                    item.appendTo(item.parent()).show($this.cfg.effect, {}, 'fast', function() {
                        movedItemsCount++;
                        
                        if(itemsToMoveCount === movedItemsCount) {
                            $this.saveState();
                            $this.fireReorderEvent();
                        }
                    });
                });
            }
            else {
                itemsToMoveCount--;
            }
        });
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }
    
        return false;
    },
    
    fireItemSelectEvent: function(item, e) {
        if(this.hasBehavior('select')) {
            var itemSelectBehavior = this.cfg.behaviors['select'],
            ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()},
                    {name: this.id + '_metaKey', value: e.metaKey},
                    {name: this.id + '_ctrlKey', value: e.ctrlKey}
                ]
            };

            itemSelectBehavior.call(this, ext);
        }
    },
    
    fireItemUnselectEvent: function(item) {
        if(this.hasBehavior('unselect')) {
            var itemUnselectBehavior = this.cfg.behaviors['unselect'],
            ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()}
                ]
            };

            itemUnselectBehavior.call(this, ext);
        }
    },
    
    fireReorderEvent: function() {
        if(this.hasBehavior('reorder')) {
            this.cfg.behaviors['reorder'].call(this);
        }
    }

});
/**
 * PrimeFaces OutputPanel Widget
 */
PrimeFaces.widget.OutputPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.cfg.global = this.cfg.global||false;
        
        if(this.cfg.deferred) {
            if(this.cfg.deferredMode === 'load') {
                this.loadContent();
            }
            else if(this.cfg.deferredMode === 'visible') {
                if(this.visible())
                    this.loadContent();
                else
                    this.bindScrollMonitor();
            }
        }
    },
            
    loadContent: function() {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            async: true,
            ignoreAutoUpdate: true,
            global: this.cfg.global,
            params: [
                {name: this.id + '_load', value: true}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.jq.html(content);
                        }
                    });

                return true;
            },
            onerror: function(xhr, status, errorThrown) {
                $this.jq.html('');
            }
        };
        
        if(this.cfg.delay) {
            setTimeout(function() {
                PrimeFaces.ajax.Request.handle(options);
            }, parseInt(this.cfg.delay));
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
            
    bindScrollMonitor: function() {
        var $this = this,
        win = $(window);
        win.off('scroll.' + this.id).on('scroll.' + this.id, function() {
            if($this.visible()) {
                $this.unbindScrollMonitor();
                $this.loadContent();
            }
        });
    },
            
    visible: function() {
        var win = $(window),
        scrollTop = win.scrollTop(),
        height = win.height(),
        top = this.jq.offset().top,
        bottom = top + this.jq.innerHeight();

        if((top >= scrollTop && top <= (scrollTop + height)) || (bottom >= scrollTop && bottom <= (scrollTop + height))) {
            return true;
        }
    },
            
    unbindScrollMonitor: function() {
        $(window).off('scroll.' + this.id);
    }
});
/**
 * PrimeFaces OverlayPanel Widget
 */
PrimeFaces.widget.OverlayPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.content = this.jq.children('div.ui-overlaypanel-content')

        //configuration
        this.cfg.my = this.cfg.my||'left top';
        this.cfg.at = this.cfg.at||'left bottom';
        this.cfg.showEvent = this.cfg.showEvent||'click.ui-overlaypanel';
        this.cfg.hideEvent = this.cfg.hideEvent||'click.ui-overlaypanel';
        this.cfg.dismissable = (this.cfg.dismissable === false) ? false : true;
        
        if(this.cfg.showCloseIcon) {
            this.closerIcon = $('<a href="#" class="ui-overlaypanel-close ui-state-default" href="#"><span class="ui-icon ui-icon-closethick"></span></a>').appendTo(this.jq);
        }
        
        //prevent duplicate elements
        if(this.jq.length > 1) {
            $(document.body).children(this.jqId).remove();
            this.jq = $(this.jqId);
        }
        
        if(this.cfg.appendToBody) {
            this.jq.appendTo(document.body);
        }
        
        this.bindCommonEvents();

        if(this.cfg.target) {
            this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);
            this.bindTargetEvents();
            
            //dialog support
            this.setupDialogSupport();
        }
    },
    
    bindTargetEvents: function() {
        var $this = this;

        //mark target and descandants of target as a trigger for a primefaces overlay
        this.target.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);

        //show and hide events for target
        if(this.cfg.showEvent === this.cfg.hideEvent) {
            var event = this.cfg.showEvent;
            
            this.target.on(event, function(e) {
                $this.toggle();
            });
        }
        else {
            var showEvent = this.cfg.showEvent + '.ui-overlaypanel',
            hideEvent = this.cfg.hideEvent + '.ui-overlaypanel';
            
            this.target.off(showEvent + ' ' + hideEvent).on(showEvent, function(e) {
                if(!$this.isVisible()) {
                    $this.show();
                    if(showEvent === 'contextmenu.ui-overlaypanel') {
                        e.preventDefault();
                    }
                }
            })
            .on(hideEvent, function(e) {
                if($this.isVisible()) {
                    $this.hide();
                }
            });
        }
        
        $this.target.off('keydown.ui-overlaypanel keyup.ui-overlaypanel').on('keydown.ui-overlaypanel', function(e) {
            var keyCode = $.ui.keyCode, key = e.which;
            
            if(key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER) {
                e.preventDefault();
            }
        })
        .on('keyup.ui-overlaypanel', function(e) {
            var keyCode = $.ui.keyCode, key = e.which;
            
            if(key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER) {
                $this.toggle();
                e.preventDefault();
            }
        });
    },
    
    bindCommonEvents: function() {
        var $this = this;
        
        if(this.cfg.showCloseIcon) {
            this.closerIcon.on('mouseover.ui-overlaypanel', function() {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseout.ui-overlaypanel', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.ui-overlaypanel', function(e) {
                $this.hide();
                e.preventDefault();
            });
        }
        
        //hide overlay when mousedown is at outside of overlay
        if(this.cfg.dismissable) {
            var hideNS = 'mousedown.' + this.id;
            $(document.body).off(hideNS).on(hideNS, function (e) {
                if($this.jq.hasClass('ui-overlay-hidden')) {
                    return;
                }

                //do nothing on target mousedown
                if($this.target) {
                    var target = $(e.target);
                    if($this.target.is(target)||$this.target.has(target).length > 0) {
                        return;
                    }
                }

                //hide overlay if mousedown is on outside
                var offset = $this.jq.offset();
                if(e.pageX < offset.left ||
                    e.pageX > offset.left + $this.jq.outerWidth() ||
                    e.pageY < offset.top ||
                    e.pageY > offset.top + $this.jq.outerHeight()) {

                    $this.hide();
                }
            }); 
        }

        //Hide overlay on resize
        var resizeNS = 'resize.' + this.id;
        $(window).off(resizeNS).on(resizeNS, function() {
            if($this.jq.hasClass('ui-overlay-visible')) {
                $this.align();
            }
        });
    },
    
    toggle: function() {
        if(!this.isVisible())
            this.show();
        else
            this.hide();
    },
    
    show: function(target) {
        if(!this.loaded && this.cfg.dynamic)
            this.loadContents(target);
        else
            this._show(target);
    },
    
    _show: function(target) {
        var $this = this;

        this.align(target);

        //replace visibility hidden with display none for effect support, toggle marker class
        this.jq.removeClass('ui-overlay-hidden').addClass('ui-overlay-visible').css({
            'display':'none'
            ,'visibility':'visible'
        });

        if(this.cfg.showEffect) {
            this.jq.show(this.cfg.showEffect, {}, 200, function() {
                $this.postShow();
            });
        }
        else {
            this.jq.show();
            this.postShow();
        }
    },
    
    align: function(target) {
        var fixedPosition = this.jq.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null,
        targetId = target||this.cfg.target;

        this.jq.css({'left':'', 'top':'', 'z-index': ++PrimeFaces.zindex})
                .position({
                    my: this.cfg.my
                    ,at: this.cfg.at
                    ,of: document.getElementById(targetId)
                    ,offset: positionOffset
                });
    },
    
    hide: function() {
        var $this = this;

        if(this.cfg.hideEffect) {
            this.jq.hide(this.cfg.hideEffect, {}, 200, function() {
                $this.postHide();
            });
        }
        else {
            this.jq.hide();
            this.postHide();
        }
    },
    
    postShow: function() {
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
        
        this.applyFocus();
    },
    
    postHide: function() {
        //replace display block with visibility hidden for hidden container support, toggle marker class
        this.jq.removeClass('ui-overlay-visible').addClass('ui-overlay-hidden').css({
            'display':'block'
            ,'visibility':'hidden'
        });
        
        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    },
    
    setupDialogSupport: function() {
        var dialog = this.target.closest('.ui-dialog');

        if(dialog.length == 1) {
            //set position as fixed to scroll with dialog
            this.jq.css('position', 'fixed');

            //append to body if not already appended by user choice
            if(!this.cfg.appendToBody) {
                this.jq.appendTo(document.body);
            }
        }
    },
    
    loadContents: function(target) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                            this.loaded = true;
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this._show(target);
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },
    
    isVisible: function() {
        return this.jq.hasClass('ui-overlay-visible');
    },
    
    applyFocus: function() {
        this.jq.find(':not(:submit):not(:button):input:visible:enabled:first').focus();
    }

});
PrimeFaces.widget.Paginator = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.jq = $();

        var _self = this;
        $.each(this.cfg.id, function(index, id){
            _self.jq = _self.jq.add($(PrimeFaces.escapeClientId(id)));
        });

        //elements
        this.pagesContainer = this.jq.children('.ui-paginator-pages');
        this.pageLinks = this.pagesContainer.children('.ui-paginator-page');
        this.rppSelect = this.jq.children('.ui-paginator-rpp-options');
        this.jtpSelect = this.jq.children('.ui-paginator-jtp-select');
        this.firstLink = this.jq.children('.ui-paginator-first');
        this.prevLink  = this.jq.children('.ui-paginator-prev');
        this.nextLink  = this.jq.children('.ui-paginator-next');
        this.endLink   = this.jq.children('.ui-paginator-last');
        this.currentReport = this.jq.children('.ui-paginator-current');

        //metadata
        this.cfg.rows = this.cfg.rows == 0 ? this.cfg.rowCount : this.cfg.rows;
        this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows)||1;
        this.cfg.pageLinks = this.cfg.pageLinks||10;
        this.cfg.currentPageTemplate = this.cfg.currentPageTemplate||'({currentPage} of {totalPages})';

        //event bindings
        this.bindEvents();
    },
            
    bindEvents: function(){
        var $this = this;

        //visuals for first,prev,next,last buttons
        this.jq.children('span.ui-state-default').on('mouseover.paginator', function(){
            var item = $(this);
            if(!item.hasClass('ui-state-disabled')) {
                item.addClass('ui-state-hover');
            }
        })
        .on('mouseout.paginator', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('focus.paginator', function() {
            var item = $(this);
            if(!item.hasClass('ui-state-disabled')) {
                item.addClass('ui-state-focus');
            }
        })
        .on('blur.paginator', function() {
            $(this).removeClass('ui-state-focus');
        })
        .on('keydown.paginator', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
                $(this).trigger('click');
                e.preventDefault();
            }
        });

        //page links
        this.bindPageLinkEvents();

        //records per page selection
        PrimeFaces.skinSelect(this.rppSelect);
        this.rppSelect.change(function(e) {
            if(!$(this).hasClass("ui-state-disabled")){
                $this.setRowsPerPage(parseInt($(this).val()));
            }
        });

        //jump to page
        PrimeFaces.skinSelect(this.jtpSelect);
        this.jtpSelect.change(function(e) {
            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage(parseInt($(this).val()));
            }
        });

        //First page link
        this.firstLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage(0);
            }
        });

        //Prev page link
        this.prevLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.page - 1);
            }
        });

        //Next page link
        this.nextLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.page + 1);
            }
        });

        //Last page link
        this.endLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.pageCount - 1);
            }
        });
    },
            
    bindPageLinkEvents: function(){
        var $this = this;

        this.pagesContainer.children('.ui-paginator-page').on('click.paginator', function(e) {
            var link = $(this);

            if(!link.hasClass('ui-state-disabled')&&!link.hasClass('ui-state-active')) {
                $this.setPage(parseInt(link.text()) - 1);
            }
        })
        .on('mouseover.paginator', function() {
            var item = $(this);
            if(!item.hasClass('ui-state-disabled')&&!item.hasClass('ui-state-active')) {
                item.addClass('ui-state-hover');
            }
        })
        .on('mouseout.paginator', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('focus.paginator', function() {
            $(this).addClass('ui-state-focus');
        })
        .on('blur.paginator', function() {
            $(this).removeClass('ui-state-focus');
        })
        .on('keydown.paginator', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
                $(this).trigger('click');
                e.preventDefault();
            }
        });
    },
    
    updateUI: function() {  
        //boundaries
        if(this.cfg.page === 0) {
            this.disableElement(this.firstLink);
            this.disableElement(this.prevLink);
        }
        else {
            this.enableElement(this.firstLink);
            this.enableElement(this.prevLink);
        }

        if(this.cfg.page === (this.cfg.pageCount - 1)) {
            this.disableElement(this.nextLink);
            this.disableElement(this.endLink);
        }
        else {
            this.enableElement(this.nextLink);
            this.enableElement(this.endLink);
        }

        //current page report
        var startRecord = (this.cfg.rowCount === 0) ? 0 : (this.cfg.page * this.cfg.rows) + 1,
        endRecord = (this.cfg.page * this.cfg.rows) + this.cfg.rows;
        if(endRecord > this.cfg.rowCount) {
            endRecord = this.cfg.rowCount;
        }

        var text = this.cfg.currentPageTemplate
            .replace("{currentPage}", this.cfg.page + 1)
            .replace("{totalPages}", this.cfg.pageCount)
            .replace("{totalRecords}", this.cfg.rowCount)
            .replace("{startRecord}", startRecord)
            .replace("{endRecord}", endRecord);
        this.currentReport.text(text);

        //rows per page dropdown
        this.rppSelect.children('option').prop('selected', false).filter('option[value=' + this.cfg.rows + ']').prop('selected', true);

        //jump to page dropdown
        if(this.jtpSelect.length > 0) {
            this.jtpSelect.children().remove();

            for(var i=0; i < this.cfg.pageCount; i++) {
                this.jtpSelect.append("<option value=" + i + ">" + (i + 1) + "</option>");
            }
            this.jtpSelect.children('option[value=' + (this.cfg.page) + ']').prop('selected','selected');
        }

        //page links
        this.updatePageLinks();
    },
            
    updatePageLinks: function() {
        var start, end, delta,
        focusedElement = $(document.activeElement),
        focusContainer, tabindex;

        if(focusedElement.hasClass('ui-paginator-page')) {
            var pagesContainerIndex = this.pagesContainer.index(focusedElement.parent());
            if(pagesContainerIndex >= 0) {
                focusContainer = this.pagesContainer.eq(pagesContainerIndex);
                tabindex = focusedElement.index();
            }
        }

        //calculate visible page links
        this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows)||1;
        var visiblePages = Math.min(this.cfg.pageLinks, this.cfg.pageCount);

        //calculate range, keep current in middle if necessary
        start = Math.max(0, Math.ceil(this.cfg.page - ((visiblePages) / 2)));
        end = Math.min(this.cfg.pageCount - 1, start + visiblePages - 1);

        //check when approaching to last page
        delta = this.cfg.pageLinks - (end - start + 1);
        start = Math.max(0, start - delta);

        //update dom
        this.pagesContainer.children().remove();
        for(var i = start; i <= end; i++) {
            var styleClass = 'ui-paginator-page ui-state-default ui-corner-all';
            if(this.cfg.page == i) {
                styleClass += " ui-state-active";
            }

            this.pagesContainer.append('<span class="' + styleClass + '" tabindex="0">' + (i + 1) + '</span>')   
        }
        
        if(focusContainer) {
            focusContainer.children().eq(tabindex).trigger('focus');
        }

        this.bindPageLinkEvents();
    },
            
    setPage: function(p, silent) {
        if(p >= 0 && p < this.cfg.pageCount && this.cfg.page != p){        
            var newState = {
                first: this.cfg.rows * p,
                rows: this.cfg.rows,
                page: p
            };

            if(silent) {
                this.cfg.page = p;
                this.updateUI();
            }
            else {
                this.cfg.paginate.call(this, newState);
            }
        }
    },
            
    setRowsPerPage: function(rpp) {
        var first = this.cfg.rows * this.cfg.page,
        page = parseInt(first / rpp);

        this.cfg.rows = rpp;

        this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows);

        this.cfg.page = -1;
        this.setPage(page);
    },
            
    setTotalRecords: function(value) {
        this.cfg.rowCount = value;
        this.cfg.pageCount = Math.ceil(value / this.cfg.rows)||1;
        this.cfg.page = 0;
        this.updateUI();
    },
            
    getCurrentPage: function() {
        return this.cfg.page;
    },
    
    getFirst: function() {
        return (this.cfg.rows * this.cfg.page);
    },
    
    getRows: function() {
        return this.cfg.rows;
    },
            
    getContainerHeight: function(margin) {
        var height = 0;
        
        for(var i = 0; i < this.jq.length; i++) {
            height += this.jq.eq(i).outerHeight(margin);
        }
        
        return height;
    },
            
    disableElement: function(element) {
        element.removeClass('ui-state-hover ui-state-focus ui-state-active').addClass('ui-state-disabled').attr('tabindex', -1);
        element.removeClass('ui-state-hover ui-state-focus ui-state-active').addClass('ui-state-disabled').attr('tabindex', -1);
    },
            
    enableElement: function(element) {
        element.removeClass('ui-state-disabled').attr('tabindex', 0);
    },
    
    next: function() {
        this.setPage(this.cfg.page + 1);
    },
    
    prev: function() {
        this.setPage(this.cfg.page - 1);
    }
});

/**
 * PrimeFaces PickList Widget
 */
PrimeFaces.widget.PickList = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.sourceList = this.jq.find('ul.ui-picklist-source');
        this.targetList = this.jq.find('ul.ui-picklist-target');
        this.sourceInput = $(this.jqId + '_source');
        this.targetInput = $(this.jqId + '_target');
        this.items = this.jq.find('.ui-picklist-item:not(.ui-state-disabled)');
        if(this.cfg.showCheckbox) {
            this.checkboxes = this.items.find('div.ui-chkbox > div.ui-chkbox-box');
        }
                
        //generate input options
        this.generateItems(this.sourceList, this.sourceInput);
        this.generateItems(this.targetList, this.targetInput);

        if(this.cfg.disabled) {
            $(this.jqId + ' li.ui-picklist-item').addClass('ui-state-disabled');
            $(this.jqId + ' button').attr('disabled', 'disabled').addClass('ui-state-disabled');
        }
        else {
            var $this = this,
                reordered = true;

            //Sortable lists
            $(this.jqId + ' ul').sortable({
                cancel: '.ui-state-disabled,.ui-chkbox-box',
                connectWith: this.jqId + ' .ui-picklist-list',
                revert: 1,
                update: function(event, ui) {
                    $this.unselectItem(ui.item);

                    $this.saveState();
                    if(reordered) {
                        $this.fireReorderEvent();
                        reordered = false;
                    }
                },
                receive: function(event, ui) {
                    $this.fireTransferEvent(ui.item, ui.sender, ui.item.parents('ul.ui-picklist-list:first'), 'dragdrop');
                },
                
                start: function(event, ui) {
                    $this.itemListName = $this.getListName(ui.item);
                    $this.dragging = true;
                },
                
                stop: function(event, ui) {
                    $this.dragging = false;
                },
                
                beforeStop:function(event, ui) {
                    if($this.itemListName !== $this.getListName(ui.item)) {
                        reordered = false;
                    }
                    else {
                        reordered = true;
                    }
                }
            });
            
            this.bindItemEvents();

            this.bindButtonEvents();
            
            this.bindFilterEvents();
        }
    },
    
    bindItemEvents: function() {
        var $this = this;
        
        this.items.on('mouseover.pickList', function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight')) {
                $(this).addClass('ui-state-hover');
            }
        })
        .on('mouseout.pickList', function(e) {
            $(this).removeClass('ui-state-hover');
        })
        .on('click.pickList', function(e) {
            //stop propagation
            if($this.checkboxClick||$this.dragging) {
                $this.checkboxClick = false;
                return;
            }
            
            var item = $(this),
            metaKey = (e.metaKey||e.ctrlKey);
            
            if(!e.shiftKey) {
                if(!metaKey) {
                    $this.unselectAll();
                }

                if(metaKey && item.hasClass('ui-state-highlight')) {
                    $this.unselectItem(item, true);
                } 
                else {
                    $this.selectItem(item, true);
                    $this.cursorItem = item;
                }
            }
            else {
                $this.unselectAll();
                
                if($this.cursorItem && ($this.cursorItem.parent().is(item.parent()))) {
                    var currentItemIndex = item.index(),
                    cursorItemIndex = $this.cursorItem.index(),
                    startIndex = (currentItemIndex > cursorItemIndex) ? cursorItemIndex : currentItemIndex,
                    endIndex = (currentItemIndex > cursorItemIndex) ? (currentItemIndex + 1) : (cursorItemIndex + 1),
                    parentList = item.parent();
                    
                    for(var i = startIndex ; i < endIndex; i++) {
                        var it = parentList.children('li.ui-picklist-item').eq(i);
                        
                        if(it.is(':visible')) {
                            if(i === (endIndex - 1))
                                $this.selectItem(it, true);
                            else
                                $this.selectItem(it);
                        }
                    }
                } 
                else {
                    $this.selectItem(item, true);
                    $this.cursorItem = item;
                }
            }
        })
        .on('dblclick.pickList', function() {
            var item = $(this);

            if($(this).parent().hasClass('ui-picklist-source'))
                $this.transfer(item, $this.sourceList, $this.targetList, 'dblclick');
            else
                $this.transfer(item, $this.targetList, $this.sourceList, 'dblclick');

            PrimeFaces.clearSelection();
        });
        
        if(this.cfg.showCheckbox) {
            this.checkboxes.on('mouseover.pickList', function(e) {
                var chkbox = $(this);
                
                if(!chkbox.hasClass('ui-state-active'))
                    chkbox.addClass('ui-state-hover');
            })
            .on('mouseout.pickList', function(e) {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.pickList', function(e) {
                $this.checkboxClick = true;
                
                var item = $(this).closest('li.ui-picklist-item');
                if(item.hasClass('ui-state-highlight')) {
                    $this.unselectItem(item, true);
                }
                else {
                    $this.selectItem(item, true);
                }
            });
        }
    },
    
    selectItem: function(item, silent) {
        item.removeClass('ui-state-hover').addClass('ui-state-highlight');
        
        if(this.cfg.showCheckbox) {
            this.selectCheckbox(item.find('div.ui-chkbox-box'));
        }
        
        if(silent) {
            this.fireItemSelectEvent(item);
        }
    },
    
    unselectItem: function(item, silent) {
        item.removeClass('ui-state-hover ui-state-highlight');
        
        if(PrimeFaces.isIE(8)) {
            item.css('filter','');
        }
        
        if(this.cfg.showCheckbox) {
            this.unselectCheckbox(item.find('div.ui-chkbox-box'));
        }
        
        if(silent) {
            this.fireItemUnselectEvent(item);
        }
    },
    
    unselectAll: function() {
        var selectedItems = this.items.filter('.ui-state-highlight');
        for(var i = 0; i < selectedItems.length; i++) {
            this.unselectItem(selectedItems.eq(i));
        }
    },
   
    selectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-hover').addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
    },
    
    unselectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
    },
        
    generateItems: function(list, input) {   
        list.children('.ui-picklist-item').each(function() {
            var item = $(this),
            itemValue = PrimeFaces.escapeHTML(item.attr('data-item-value')),
            itemLabel = item.attr('data-item-label'),
            escapedItemLabel = (itemLabel) ? PrimeFaces.escapeHTML(itemLabel) : '';
            
            input.append('<option value="' + itemValue + '" selected="selected">' + escapedItemLabel + '</option>');
        });
    },
    
    bindButtonEvents: function() {
        var _self = this;

        //visuals
        PrimeFaces.skinButton(this.jq.find('.ui-button'));

        //events
        $(this.jqId + ' .ui-picklist-button-add').click(function() {_self.add();});
        $(this.jqId + ' .ui-picklist-button-add-all').click(function() {_self.addAll();});
        $(this.jqId + ' .ui-picklist-button-remove').click(function() {_self.remove();});
        $(this.jqId + ' .ui-picklist-button-remove-all').click(function() {_self.removeAll();});

        if(this.cfg.showSourceControls) {
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-up').click(function() {_self.moveUp(_self.sourceList);});
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-top').click(function() {_self.moveTop(_self.sourceList);});
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-down').click(function() {_self.moveDown(_self.sourceList);});
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-bottom').click(function() {_self.moveBottom(_self.sourceList);});
        }

        if(this.cfg.showTargetControls) {
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-up').click(function() {_self.moveUp(_self.targetList);});
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-top').click(function() {_self.moveTop(_self.targetList);});
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-down').click(function() {_self.moveDown(_self.targetList);});
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-bottom').click(function() {_self.moveBottom(_self.targetList);});
        }
    },
        
    bindFilterEvents: function() {
        this.setupFilterMatcher();
        
        this.sourceFilter = $(this.jqId + '_source_filter');
        this.targetFilter = $(this.jqId + '_target_filter');
        var _self = this;
        
        PrimeFaces.skinInput(this.sourceFilter);
        PrimeFaces.skinInput(this.targetFilter);
        
        this.sourceFilter.on('keyup', function(e) {
            _self.filter(this.value, _self.sourceList);
        })
        .on('keydown', this.blockEnterKey);
        
        this.targetFilter.on('keyup', function(e) {
            _self.filter(this.value, _self.targetList);
        })
        .on('keydown', this.blockEnterKey);
    },
    
    blockEnterKey: function(e) {
        var key = e.which,
        keyCode = $.ui.keyCode;

        if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
            e.preventDefault();
        }
    },
    
    setupFilterMatcher: function() {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode||'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            ,'contains': this.containsFilter
            ,'endsWith': this.endsWithFilter
            ,'custom': this.cfg.filterFunction
        };
                
        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    },
    
    filter: function(value, list) {
        var filterValue = $.trim(value).toLowerCase(),
        items = list.children('li.ui-picklist-item'),
        animated = this.isAnimated();
        
        if(filterValue === '') {
            items.filter(':hidden').show();
        }
        else {
            for(var i = 0; i < items.length; i++) {
                var item = items.eq(i),
                itemLabel = item.attr('data-item-label'),
                matches = this.filterMatcher(itemLabel, filterValue);

                if(matches) {
                    if(animated)
                        item.fadeIn('fast');
                    else
                        item.show();
                }
                else {
                    if(animated)
                        item.fadeOut('fast');
                    else
                        item.hide();
                }
            }
        }
    },
    
    startsWithFilter: function(value, filter) {
        return value.toLowerCase().indexOf(filter) === 0;
    },
    
    containsFilter: function(value, filter) {
        return value.toLowerCase().indexOf(filter) !== -1;
    },
    
    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },
    
    add: function() {
        var items = this.sourceList.children('li.ui-picklist-item.ui-state-highlight')
        
        this.transfer(items, this.sourceList, this.targetList, 'command');
    },
    
    addAll: function() {
        var items = this.sourceList.children('li.ui-picklist-item:visible:not(.ui-state-disabled)');
        
        this.transfer(items, this.sourceList, this.targetList, 'command');
    },
    
    remove: function() {
        var items = this.targetList.children('li.ui-picklist-item.ui-state-highlight');
        
        this.transfer(items, this.targetList, this.sourceList, 'command');
    },
    
    removeAll: function() {
        var items = this.targetList.children('li.ui-picklist-item:visible:not(.ui-state-disabled)');
        
        this.transfer(items, this.targetList, this.sourceList, 'command');
    },
    
    moveUp: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;
            
        if(itemsCount) {    
            items.each(function() {
                var item = $(this);

                if(!item.is(':first-child')) {

                    if(animated) {
                        item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                            item.insertBefore(item.prev()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    _self.saveState();
                                    _self.fireReorderEvent();
                                }
                            });
                        });
                    }
                    else {
                        item.hide().insertBefore(item.prev()).show();
                    }

                }
            });

            if(!animated) {
                this.saveState();
                this.fireReorderEvent();
            }
        }
        
    },
    
    moveTop: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;

        if(itemsCount) {
            items.each(function() {
                var item = $(this);

                if(!item.is(':first-child')) {

                    if(animated) {
                        item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                            item.prependTo(item.parent()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function(){
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    _self.saveState();
                                    _self.fireReorderEvent();
                                }
                            });
                        });
                    }
                    else {
                        item.hide().prependTo(item.parent()).show();
                    }
                }
            });

            if(!animated) {
                this.saveState();
                this.fireReorderEvent();
            }
        }
    },
    
    moveDown: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;

        if(itemsCount) {
            $(items.get().reverse()).each(function() {
                var item = $(this);

                if(!item.is(':last-child')) {
                    if(animated) {
                        item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                            item.insertAfter(item.next()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    _self.saveState();
                                    _self.fireReorderEvent();
                                }
                            });
                        });
                    }
                    else {
                        item.hide().insertAfter(item.next()).show();
                    }
                }

            });

            if(!animated) {
                this.saveState();
                this.fireReorderEvent();
            }
        }
    },
    
    moveBottom: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;

        if(itemsCount) {
            items.each(function() {
                var item = $(this);

                if(!item.is(':last-child')) {

                    if(animated) {
                        item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                            item.appendTo(item.parent()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    _self.saveState();
                                    _self.fireReorderEvent();
                                }
                            });
                        });
                    }
                    else {
                        item.hide().appendTo(item.parent()).show();
                    }
                }

            });

            if(!animated) {
                this.saveState();
                this.fireReorderEvent();
            }
        }
    },
    
    /**
     * Clear inputs and repopulate them from the list states 
     */ 
    saveState: function() {
        this.sourceInput.children().remove();
        this.targetInput.children().remove();

        this.generateItems(this.sourceList, this.sourceInput);
        this.generateItems(this.targetList, this.targetInput);
        this.cursorItem = null;
    },
    
    transfer: function(items, from, to, type) {  
        var $this = this,
        itemsCount = items.length,
        transferCount = 0;
        
        if(this.isAnimated()) {
            items.hide(this.cfg.effect, {}, this.cfg.effectSpeed, function() {
                var item = $(this);
                $this.unselectItem(item);

                item.appendTo(to).show($this.cfg.effect, {}, $this.cfg.effectSpeed, function() {
                    transferCount++;

                    //fire transfer when all items are transferred
                    if(transferCount == itemsCount) {
                        $this.saveState();
                        $this.fireTransferEvent(items, from, to, type);
                    }
                });
            });
        }
        else {
            items.hide();
            
            if(this.cfg.showCheckbox) {
                items.each(function() {
                    $this.unselectItem($(this));
                });
            }
            
            items.appendTo(to).show();
            
            this.saveState();
            this.fireTransferEvent(items, from, to, type);
        }
    },
    
    /**
     * Fire transfer ajax behavior event
     */
    fireTransferEvent: function(items, from, to, type) {
        if(this.cfg.onTransfer) {
            var obj = {};
            obj.items = items;
            obj.from = from;
            obj.to = to;
            obj.type = type;

            this.cfg.onTransfer.call(this, obj);
        }
        
        if(this.cfg.behaviors) {
            var transferBehavior = this.cfg.behaviors['transfer'];

            if(transferBehavior) {
                var ext = {
                    params: []
                },
                paramName = this.id + '_transferred',
                isAdd = from.hasClass('ui-picklist-source');
                
                items.each(function(index, item) {
                    ext.params.push({name:paramName, value:$(item).attr('data-item-value')});
                });
                
                ext.params.push({name:this.id + '_add', value:isAdd});

                transferBehavior.call(this, ext);
            }
        }
    },
    
    getListName: function(element){
        return element.parent().hasClass("ui-picklist-source") ? "source" : "target";
    },
            
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }
    
        return false;
    },
    
    fireItemSelectEvent: function(item) {
        if(this.hasBehavior('select')) {
            var itemSelectBehavior = this.cfg.behaviors['select'],
            ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()},
                    {name: this.id + '_listName', value: this.getListName(item)}
                ]
            };

            itemSelectBehavior.call(this, ext);
        }
    },
    
    fireItemUnselectEvent: function(item) {
        if(this.hasBehavior('unselect')) {
            var itemUnselectBehavior = this.cfg.behaviors['unselect'],
            ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()},
                    {name: this.id + '_listName', value: this.getListName(item)}
                ]
            };

            itemUnselectBehavior.call(this, ext);
        }
    },
    
    fireReorderEvent: function() {
        if(this.hasBehavior('reorder')) {
            this.cfg.behaviors['reorder'].call(this);
        }
    },
            
    isAnimated: function() {
        return (this.cfg.effect && this.cfg.effect != 'none');
    }

});
/**
 * PrimeFaces ProgressBar widget
 */
PrimeFaces.widget.ProgressBar = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.jqValue = this.jq.children('.ui-progressbar-value');
        this.jqLabel = this.jq.children('.ui-progressbar-label');
        this.value = this.cfg.initialValue;
        this.cfg.global = (this.cfg.global === false) ? false : true;

        if(this.cfg.ajax) {
            this.cfg.formId = this.jq.closest('form').attr('id');
        }

        this.enableARIA();
    },
    
    setValue: function(value) {
        if(value >= 0 && value<=100) {
            if(value == 0) {
                this.jqValue.hide().css('width', '0%').removeClass('ui-corner-right');

                this.jqLabel.hide();
            }
            else {
                this.jqValue.show().animate({
                    'width': value + '%' 
                }, 500, 'easeInOutCirc');

                if(this.cfg.labelTemplate) {
                    var formattedLabel = this.cfg.labelTemplate.replace(/{value}/gi, value);

                    this.jqLabel.html(formattedLabel).show();
                }
            }

            this.value = value;
            this.jq.attr('aria-valuenow', value);
        }
    },
    
    getValue: function() {
        return this.value;
    },
    
    start: function() {
        var $this = this;

        if(this.cfg.ajax) {

            this.progressPoll = setInterval(function() {
                var options = {
                    source: $this.id,
                    process: $this.id,
                    formId: $this.cfg.formId,
                    global: $this.cfg.global,
                    async: true,
                    oncomplete: function(xhr, status, args) {
                        var value = args[$this.id + '_value'];
                        $this.setValue(value);

                        //trigger complete listener
                        if(value === 100) {
                            $this.fireCompleteEvent();
                        }
                    }
                };

                PrimeFaces.ajax.AjaxRequest(options);

            }, this.cfg.interval);
        }
    },
    
    fireCompleteEvent: function() {
        clearInterval(this.progressPoll);

        if(this.cfg.behaviors) {
            var completeBehavior = this.cfg.behaviors['complete'];

            if(completeBehavior) {
                completeBehavior.call(this);
            }
        }
    },
    
    cancel: function() {
        clearInterval(this.progressPoll);
        this.setValue(0);
    },
    
    enableARIA: function() {
        this.jq.attr('role', 'progressbar')
                .attr('aria-valuemin', 0)
                .attr('aria-valuenow', this.value)
                .attr('aria-valuemax', 100);
    }

});
/**
 * PrimeFaces Rating Widget
 */
PrimeFaces.widget.Rating = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.jqInput = $(this.jqId + '_input');
        this.value = this.getValue();
        this.stars = this.jq.children('.ui-rating-star');
        this.cancel = this.jq.children('.ui-rating-cancel');
        
        if(!this.cfg.disabled && !this.cfg.readonly) {
            this.bindEvents();
        }
        
        if(this.cfg.readonly) {
            this.jq.children().css('cursor', 'default');
        }
    },
    
    bindEvents: function() {
        var _self = this;
        
        this.stars.click(function() {
            var value = _self.stars.index(this) + 1;   //index starts from zero
            
            _self.setValue(value);
        });
        
        this.cancel.hover(function() {
            $(this).toggleClass('ui-rating-cancel-hover');
        })
        .click(function() {
            _self.reset();
        });
    },
    
    unbindEvents: function() {        
        this.stars.unbind('click');
        
        this.cancel.unbind('hover click');
    },
    
    getValue: function() {
        var inputVal = this.jqInput.val();
        
        return inputVal == '' ? null : parseInt(inputVal);
    },
    
    setValue: function(value) {
        //set hidden value
        this.jqInput.val(value);
        
        //update visuals
        this.stars.removeClass('ui-rating-star-on');
        for(var i = 0; i < value; i++) {
            this.stars.eq(i).addClass('ui-rating-star-on');
        }
        
        //invoke callback
        if(this.cfg.onRate) {
            this.cfg.onRate.call(this, value);
        }

        //invoke ajax rate behavior
        if(this.cfg.behaviors) {
            var rateBehavior = this.cfg.behaviors['rate'];
            if(rateBehavior) {
                rateBehavior.call(this);
            }
        }
    },
    
    enable: function() {
        this.cfg.disabled = false;
        
        this.bindEvents();
        
        this.jq.removeClass('ui-state-disabled');
    },
    
    disable: function() {
        this.cfg.disabled = true;
        
        this.unbindEvents();
        
        this.jq.addClass('ui-state-disabled');
    },
    
    reset: function() {
        this.jqInput.val('');
        
        this.stars.filter('.ui-rating-star-on').removeClass('ui-rating-star-on');
        
        //invoke ajax cancel behavior
        if(this.cfg.behaviors) {
            var cancelBehavior = this.cfg.behaviors['cancel'];
            if(cancelBehavior) {
                cancelBehavior.call(this);
            }
        }
    }
});
/** 
 * PrimeFaces Resizable Widget
 */
PrimeFaces.widget.Resizable = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));

        if(this.cfg.ajaxResize) {
            this.cfg.formId = $(this.target).parents('form:first').attr('id');
        }
        
        if (this.cfg.isContainment) {
        	this.cfg.containment = PrimeFaces.escapeClientId(this.cfg.parentComponentId);
        }

        var _self = this;

        this.cfg.stop = function(event, ui) {
            if(_self.cfg.onStop) {
                _self.cfg.onStop.call(_self, event, ui);
            }

            _self.fireAjaxResizeEvent(event, ui);
        }

        this.cfg.start = function(event, ui) {
            if(_self.cfg.onStart) {
                _self.cfg.onStart.call(_self, event, ui);
            }
        }

        this.cfg.resize = function(event, ui) {
            if(_self.cfg.onResize) {
                _self.cfg.onResize.call(_self, event, ui);
            }
        }

        this.jqTarget.resizable(this.cfg);
        
        this.removeScriptElement(this.id);
    },
    
    fireAjaxResizeEvent: function(event, ui) {
        if(this.cfg.behaviors) {
            var resizeBehavior = this.cfg.behaviors['resize'];
            if(resizeBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_width', value: parseInt(ui.helper.width())},
                        {name: this.id + '_height', value: parseInt(ui.helper.height())}
                    ]
                };

                resizeBehavior.call(this, ext);
            }
        }
    }
    
});
/**
 * PrimeFaces Slider Widget
 */
PrimeFaces.widget.Slider = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.displayTemplate = this.cfg.displayTemplate||(this.cfg.range ? '{min} - {max}' : '{value}');
        
        if(this.cfg.range) {
            var inputIds = this.cfg.input.split(',');
            this.input = $(PrimeFaces.escapeClientId(inputIds[0]) + ',' + PrimeFaces.escapeClientId(inputIds[1]));
        } 
        else {
            this.input = $(PrimeFaces.escapeClientId(this.cfg.input));
        }
        
        if(this.cfg.display) {
            this.output = $(PrimeFaces.escapeClientId(this.cfg.display));
        }

        this.jq.slider(this.cfg);

        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        this.jq.bind('slide', function(event, ui) {
            $this.onSlide(event, ui);
        });

        if(this.cfg.onSlideStart) {
            this.jq.bind('slidestart', function(event, ui) {
                $this.cfg.onSlideStart.call(this, event, ui);
            });
        }

        this.jq.bind('slidestop', function(event, ui) {
            $this.onSlideEnd(event, ui);
        });

        this.input.on('keydown.slider', function (e) { 
            var keyCode = $.ui.keyCode, 
            key = e.which;
            
            switch(key) { 
                case keyCode.UP: 
                case keyCode.DOWN: 
                case keyCode.LEFT: 
                case keyCode.RIGHT:                
                case keyCode.BACKSPACE: 
                case keyCode.DELETE: 
                case keyCode.END: 
                case keyCode.HOME:
                case keyCode.TAB: 
                break; 
            
                default:
                    var metaKey = e.metaKey||e.ctrlKey, 
                    isNumber = (key >= 48 && key <= 57) || (key >= 96 && key <= 105);
                    
                    //prevent special characters with alt and shift
                    if(e.altKey || (e.shiftKey && !(key === keyCode.UP || key === keyCode.DOWN || key === keyCode.LEFT || key === keyCode.RIGHT))) {
                        e.preventDefault();
                    }
                    
                    //prevent letters and allow letters with meta key such as ctrl+c
                    if(!isNumber && !metaKey) { 
                        e.preventDefault(); 
                    }
                break; 
            } 
        }).on('keyup.slider', function (e) { 
            $this.setValue($this.input.val()); 
        });
    },
    
    onSlide: function(event, ui) {
        if(this.cfg.onSlide) {
            this.cfg.onSlide.call(this, event, ui);
        }

        if(this.cfg.range) {
            this.input.eq(0).val(ui.values[0]);
            this.input.eq(1).val(ui.values[1]);
            
            if(this.output) {
                this.output.html(this.cfg.displayTemplate.replace('{min}', ui.values[0]).replace('{max}', ui.values[1]));
            }
        }
        else {
            this.input.val(ui.value);
            
            if(this.output) {
                this.output.html(this.cfg.displayTemplate.replace('{value}', ui.value));
            }
        }
    },
    
    onSlideEnd: function(event, ui) {
        if(this.cfg.onSlideEnd) {
            this.cfg.onSlideEnd.call(this, event, ui);
        }

        if(this.cfg.behaviors) {
            var slideEndBehavior = this.cfg.behaviors['slideEnd'];

            if(slideEndBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_slideValue', value: ui.value}
                    ]
                };

                slideEndBehavior.call(this, ext);
            }
        }
    },
    
    getValue: function() {
        return this.jq.slider('value');
    },
    
    setValue: function(value) {
        this.jq.slider('value', value);
    },
    
    getValues: function() {
        return this.jq.slider('values');
    },
    
    setValues: function(values) {
        this.jq.slider('values', values);
    },
    
    enable: function() {
        this.jq.slider('enable');
    },
    
    disable: function() {
        this.jq.slider('disable');
    }
});
/**
 * PrimeFaces Spinner Widget
 */
PrimeFaces.widget.Spinner = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.input = this.jq.children('.ui-spinner-input');
        this.upButton = this.jq.children('a.ui-spinner-up');
        this.downButton = this.jq.children('a.ui-spinner-down');
        this.cfg.step = this.cfg.step||1;
        this.cursorOffset = this.cfg.prefix ? this.cfg.prefix.length: 0;
        if(parseInt(this.cfg.step) === 0) {
            this.cfg.precision = this.cfg.step.toString().split(/[,]|[.]/)[1].length;
        }
        
        var maxlength = this.input.attr('maxlength');
        if(maxlength) {
            this.cfg.maxlength = parseInt(maxlength);
        }
        
        this.updateValue();

        this.addARIA();

        if(this.input.prop('disabled')||this.input.prop('readonly')) {
            return;
        }

        this.bindEvents();
                
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        PrimeFaces.skinInput(this.input);
    },
    
    bindEvents: function() {
        var $this = this;

        this.jq.children('.ui-spinner-button')
            .on('mouseover.spinner', function() {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseout.spinner', function() {
                $(this).removeClass('ui-state-hover ui-state-active');

                if($this.timer) {
                    clearInterval($this.timer);
                }
            })
            .on('mouseup.spinner', function() {
                clearInterval($this.timer);
                $(this).removeClass('ui-state-active').addClass('ui-state-hover');
                $this.input.trigger('change');
            })
            .on('mousedown.spinner', function(e) {
                var element = $(this),
                dir = element.hasClass('ui-spinner-up') ? 1 : -1;

                element.removeClass('ui-state-hover').addClass('ui-state-active');
                
                if($this.input.is(':not(:focus)')) {
                    $this.input.focus();
                }

                $this.repeat(null, dir);

                //keep focused
                e.preventDefault();
        });

        this.input.on('keydown.spinner', function (e) {        
            var keyCode = $.ui.keyCode;
            
            switch(e.which) {            
                case keyCode.UP:
                    $this.spin(1);
                break;

                case keyCode.DOWN:
                    $this.spin(-1);
                break;
                
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:    
                    $this.updateValue();
                    $this.format();
                break;

                default:
                    //do nothing
                break;
            }
        })
        .on('keyup.spinner', function (e) { 
            $this.updateValue();
    
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.UP||e.which === keyCode.DOWN) {
                $this.input.trigger('change');
            }
        })
        .on('blur.spinner', function(e) {
            $this.format();
        })
        .on('mousewheel.spinner', function(event, delta) {
            if($this.input.is(':focus')) {
                if(delta > 0)
                    $this.spin(1);
                else
                    $this.spin(-1);
                
                return false;
            }
        });
    },
    
    repeat: function(interval, dir) {
        var $this = this,
        i = interval||500;

        clearTimeout(this.timer);
        this.timer = setTimeout(function() {
            $this.repeat(40, dir);
        }, i);

        this.spin(dir);
    },
            
    toFixed: function (value, precision) {
        var power = Math.pow(10, precision||0);
        return String(Math.round(value * power) / power);
    },
                    
    spin: function(dir) {
        var step = this.cfg.step * dir,
        currentValue = this.value ? this.value : 0,
        newValue = null;
        
        if(this.cfg.precision)
            newValue = parseFloat(this.toFixed(currentValue + step, this.cfg.precision));
        else
            newValue = parseInt(currentValue + step);
    
        if(this.cfg.maxlength !== undefined && newValue.toString().length > this.cfg.maxlength) {
            newValue = currentValue;
        }
    
        if(this.cfg.min !== undefined && newValue < this.cfg.min) {
            newValue = this.cfg.min;
        }

        if(this.cfg.max !== undefined && newValue > this.cfg.max) {
            newValue = this.cfg.max;
        }

        this.value = newValue;
        this.format();
        this.input.attr('aria-valuenow', newValue);        
    },
    
    updateValue: function() {
        var value = this.input.val();

        if($.trim(value) === '') {
            if(this.cfg.min !== undefined)
                this.value = this.cfg.min;
            else
                this.value = null;
        }
        else {
            if(this.cfg.prefix && value.indexOf(this.cfg.prefix) === 0) {
                value = value.substring(this.cfg.prefix.length, value.length);
            }  else if(this.cfg.suffix && value.indexOf(this.cfg.suffix) === (value.length - this.cfg.suffix.length)) {
                value = value.substring(0, value.length - this.cfg.suffix.length);
            }
            
            if(this.cfg.precision)
                value = parseFloat(value);
            else
                value = parseInt(value);
            
            if(!isNaN(value)) {
                if(this.cfg.max !== undefined && value > this.cfg.max) {
                    value = this.cfg.max;
                }
                
                if(this.cfg.min !== undefined && value < this.cfg.min) {
                    value = this.cfg.min;
                }
                
                this.value = value;
            }
        }
    },
       
    format: function() {
        if(this.value !== null) {
            var value = this.value;

            if(this.cfg.prefix)
                value = this.cfg.prefix + value;

            if(this.cfg.suffix)
                value = value + this.cfg.suffix;

            this.input.val(value);
        }
    },

    
    addARIA: function() {
        this.input.attr('role', 'spinner');
        this.input.attr('aria-multiline', false);
        this.input.attr('aria-valuenow', this.value);

        if(this.cfg.min !== undefined) 
            this.input.attr('aria-valuemin', this.cfg.min);

        if(this.cfg.max !== undefined) 
            this.input.attr('aria-valuemax', this.cfg.max);

        if(this.input.prop('disabled'))
            this.input.attr('aria-disabled', true);

        if(this.input.prop('readonly'))
            this.input.attr('aria-readonly', true);
    }
    
});
/**
 * PrimeFaces Spotlight Widget
 */
PrimeFaces.widget.Spotlight = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);
        this.eventsToBlock = 'focus.' + this.id + ' mousedown.' + this.id + ' mouseup.' + this.id;
    
        if(!$(document.body).children('.ui-spotlight').length) {
            this.createMasks();
        }
        
        if(this.cfg.active) {
            this.show();
        }
    },
    
    createMasks: function() {
        var documentBody = $(document.body);
        documentBody.append('<div class="ui-widget-overlay ui-spotlight ui-spotlight-top ui-helper-hidden"></div><div class="ui-widget-overlay ui-spotlight ui-spotlight-bottom ui-helper-hidden"></div>' + 
                        '<div class="ui-widget-overlay ui-spotlight ui-spotlight-left ui-helper-hidden"></div><div class="ui-widget-overlay ui-spotlight ui-spotlight-right ui-helper-hidden"></div>');
    },
    
    show: function() {
        this.calculatePositions();

        $(document.body).children('div.ui-spotlight').show();
        
        this.bindEvents();
    },
    
    calculatePositions: function() {
        var doc = $(document),
        documentBody = $(document.body),
        offset = this.target.offset();
        
        documentBody.children('div.ui-spotlight-top').css({
            'left': 0,
            'top': 0,
            'width': documentBody.width(),
            'height': offset.top
        });
        
        var bottomTop = offset.top + this.target.outerHeight();
        documentBody.children('div.ui-spotlight-bottom').css({
            'left': 0,
            'top': bottomTop,
            'width': documentBody.width(),
            'height': doc.height() - bottomTop
        });
        
        documentBody.children('div.ui-spotlight-left').css({
            'left': 0,
            'top': offset.top,
            'width': offset.left,
            'height': this.target.outerHeight()
        });
        
        var rightLeft = offset.left + this.target.outerWidth();
        documentBody.children('div.ui-spotlight-right').css({
            'left': rightLeft,
            'top': offset.top,
            'width': documentBody.width() - rightLeft,
            'height': this.target.outerHeight()
        });
    },
    
    bindEvents: function() {
        var $this = this;
        
        this.target.data('zindex',this.target.zIndex()).css('z-index', ++PrimeFaces.zindex);
        
        $(document).on('keydown.' + this.id,
                function(event) {
                    var target = $(event.target);

                    if(event.keyCode === $.ui.keyCode.TAB) {
                        var tabbables = $this.target.find(':tabbable');
                        if(tabbables.length) {
                            var first = tabbables.filter(':first'),
                            last = tabbables.filter(':last');
                    
                            if(target.is(document.body)) {
                                first.focus(1);
                                event.preventDefault();
                            }
                            else if(event.target === last[0] && !event.shiftKey) {
                                first.focus(1);
                                event.preventDefault();
                            } 
                            else if (event.target === first[0] && event.shiftKey) {
                                last.focus(1);
                                event.preventDefault();
                            }
                        }
                    }
                    else if(!target.is(document.body) && (target.zIndex() < $this.target.zIndex())) {
                        event.preventDefault();
                    }
                })
                .on(this.eventsToBlock, function(event) {
                    if ($(event.target).zIndex() < $this.target.zIndex()) {
                        event.preventDefault();
                    }
                });
                
        $(window).on('resize.spotlight', function() {
            $this.calculatePositions();
        });
    },
    
    unbindEvents: function() {
        $(document).off(this.eventsToBlock).off('keydown.' + this.id);
        $(window).off('resize.spotlight');
    },
    
    hide: function() {
        $(document.body).children('.ui-spotlight').hide();
        this.unbindEvents();
        this.target.css('z-index', this.target.zIndex());
    }

});
/**
 * PrimeFaces Sticky Widget
 */
PrimeFaces.widget.Sticky = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.target = $(PrimeFaces.escapeClientId(this.cfg.target));
        this.cfg.margin = this.cfg.margin||0;

        this.initialState = {
            top: this.target.offset().top,
            height: this.target.height()
        };

        this.bindEvents();
    },
    
    refresh: function(cfg) {        
        this.target = $(PrimeFaces.escapeClientId(this.cfg.target));
        
        if(this.fixed) {
            this.ghost.remove();
            this.fix(true);
        }
    },
    
    bindEvents: function() {
        var $this = this,
        win = $(window),
        scrollNS = 'scroll.' + this.cfg.id,
        resizeNS = 'resize.' + this.cfg.id;

        win.off(scrollNS).on(scrollNS, function() {
            if(win.scrollTop() > $this.initialState.top)
                $this.fix();
            else
                $this.restore();
        })
        .off(resizeNS).on(resizeNS, function() {
            if ($this.fixed) {
                $this.target.width($this.ghost.outerWidth() - ($this.target.outerWidth() - $this.target.width()));
            }
        });
    },
    
    fix: function(force) {
        if(!this.fixed || force) {
            this.target.css({
                'position': 'fixed',
                'top': this.cfg.margin,
                'z-index': ++PrimeFaces.zindex
            })
            .addClass('ui-shadow ui-sticky');

            this.ghost = $('<div class="ui-sticky-ghost"></div>').height(this.initialState.height).insertBefore(this.target);
            this.target.width(this.ghost.outerWidth() - (this.target.outerWidth() - this.target.width()));
            this.fixed = true;
        }
    },
    
    restore: function() {
        if(this.fixed) {
            this.target.css({
                position: 'static',
                top: 'auto',
                width: 'auto'
            })
            .removeClass('ui-shadow ui-sticky');

            this.ghost.remove();
            this.fixed = false;
        }
    }

});   
/**
 * PrimeFaces TabView Widget
 */
PrimeFaces.widget.TabView = PrimeFaces.widget.DeferredWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.panelContainer = this.jq.children('.ui-tabs-panels');
        this.stateHolder = $(this.jqId + '_activeIndex');
        this.cfg.selected = parseInt(this.stateHolder.val());
        this.focusedTabHeader = null;
        this.cfg.tabindex = this.cfg.tabindex||'0';
        
        if(this.cfg.scrollable) {
            this.navscroller = this.jq.children('.ui-tabs-navscroller');
            this.navcrollerLeft = this.navscroller.children('.ui-tabs-navscroller-btn-left');
            this.navcrollerRight = this.navscroller.children('.ui-tabs-navscroller-btn-right');
            this.navContainer = this.navscroller.children('.ui-tabs-nav');
            this.firstTab = this.navContainer.children(':first-child');
            this.lastTab = this.navContainer.children(':last-child');
            this.scrollStateHolder = $(this.jqId + '_scrollState');
        }
        else {
            this.navContainer = this.jq.children('.ui-tabs-nav');
        }

        this.navContainerItems = this.navContainer.children('li');
        for(var i = 0; i < this.navContainerItems.length; i++) {
            if(this.cfg.selected === i && !this.navContainerItems.eq(i).hasClass('ui-state-disabled')) {
                this.navContainerItems.eq(i).attr('tabindex', this.cfg.tabindex);
            }
            else {
                this.navContainerItems.eq(i).attr('tabindex', '-1');
            }
        }
        
        this.bindEvents();

        //Cache initial active tab
        if(this.cfg.dynamic && this.cfg.cache) {
            this.markAsLoaded(this.panelContainer.children().eq(this.cfg.selected));
        }
        
        this.renderDeferred();
    },
    
    //@Override
    renderDeferred: function() {     
        if(this.jq.is(':visible')) {
            this._render();
        }
        else {
            var container = this.jq.parent().closest('.ui-hidden-container'),
            $this = this;
    
            if(container.length) {
                this.addDeferredRender(this.id, container, function() {
                    return $this.render();
                });
            }
        }
    },
    
    _render: function() {
        if(this.cfg.scrollable) {
            this.initScrolling();
        }
    },
    
    bindEvents: function() {
        var $this = this;

        //Tab header events
        this.navContainer.children('li')
                .on('mouseover.tabview', function(e) {
                    var element = $(this);
                    if(!element.hasClass('ui-state-disabled')) {
                        element.addClass('ui-state-hover');
                    }
                })
                .on('mouseout.tabview', function(e) {
                    var element = $(this);
                    if(!element.hasClass('ui-state-disabled')) {
                        element.removeClass('ui-state-hover');
                    }
                })
                .on('click.tabview', function(e) {
                    var element = $(this);

                    if($(e.target).is(':not(.ui-icon-close)')) {
                        var index = element.index();

                        if(!element.hasClass('ui-state-disabled') && index !== $this.cfg.selected) {
                            $this.select(index);
                            element.trigger('focus.tabview');
                        }
                    }

                    e.preventDefault();
                });

        //Closable tabs
        this.navContainer.find('li .ui-icon-close')
            .on('click.tabview', function(e) {
                var index = $(this).parent().index();
              
                if($this.cfg.onTabClose) {
                    var retVal = $this.cfg.onTabClose.call($this, index);
                    
                    if(retVal !== false) {
                        $this.remove(index);
                    }
                }
                else {
                    $this.remove(index);
                }

                e.preventDefault();
            });

        //Scrolling
        if(this.cfg.scrollable) {
            this.navscroller.children('.ui-tabs-navscroller-btn')
                            .on('mouseover.tabview', function() {
                                var el = $(this);
                                if(!el.hasClass('ui-state-disabled'))
                                    $(this).addClass('ui-state-hover');
                            })
                            .on('mouseout.tabview', function() {
                                var el = $(this);
                                if(!el.hasClass('ui-state-disabled'))
                                    $(this).removeClass('ui-state-hover ui-state-active');
                            })
                            .on('mousedown.tabview', function() {
                                var el = $(this);
                                if(!el.hasClass('ui-state-disabled'))
                                    $(this).removeClass('ui-state-hover').addClass('ui-state-active');
                            })
                            .on('mouseup.tabview', function() {
                                var el = $(this);
                                if(!el.hasClass('ui-state-disabled'))
                                    $(this).addClass('ui-state-hover').removeClass('ui-state-active');
                            });
            
            
            this.navcrollerLeft.on('click.tabview', function(e) {
                                $this.scroll(100);
                                e.preventDefault();
                            });
                            
            this.navcrollerRight.on('click.tabview', function(e) {
                                $this.scroll(-100);
                                e.preventDefault();
                            });
        }
        
        this.bindKeyEvents();
    },
        
    bindKeyEvents: function() {
        var $this = this;
        
        this.navContainer.children('li').on('focus.tabview', function() {
            $this.focusedTabHeader = $(this);
            if(!$this.focusedTabHeader.hasClass('ui-state-disabled')) {
                $this.navContainer.children('li[tabindex="'+ $this.cfg.tabindex +'"]').attr('tabindex', '-1').removeClass('ui-tabs-outline');
                $this.focusedTabHeader.attr('tabindex', $this.cfg.tabindex).addClass('ui-tabs-outline');
            }
        })
        .on('blur.tabview', function(){
            if($this.focusedTabHeader) {
                $this.focusedTabHeader.removeClass('ui-tabs-outline');                
            }
        })
        .on('keydown.tabview', function(e) {
            var keyCode = $.ui.keyCode;
 
            switch(e.which) {
                case keyCode.LEFT:
                case keyCode.UP: 
                    if($this.focusedTabHeader) {
                        if($this.cfg.scrollable && ($this.focusedTabHeader.index() === 0)) {
                            break;
                        }
                      
                        if($this.focusedTabHeader.index() === 0) {
                            $this.focusedTabHeader = $this.navContainer.children('li:not(.ui-state-disabled):last');
                        }
                        else {
                            $this.focusedTabHeader = $this.focusedTabHeader.prevAll('li:not(.ui-state-disabled):first');
                            if(!$this.focusedTabHeader.length) {
                                $this.focusedTabHeader = $this.navContainer.children('li:not(.ui-state-disabled):last');
                            }
                        }
                        $this.focusedTabHeader.trigger('focus.tabview');

                        if($this.cfg.scrollable) {
                            var leftScroll = $this.focusedTabHeader.position().left < $this.navcrollerLeft.position().left;
                            if(leftScroll) {
                                $this.navcrollerLeft.trigger('click.tabview');
                            }
                        }
                    }
                    e.preventDefault();
                    clearTimeout($this.activating);
                    
                    $this.activating = setTimeout(function() {                
                        $this.focusedTabHeader.trigger('click');
                    }, 500);
                break;

                case keyCode.RIGHT:
                case keyCode.DOWN:
                    if($this.focusedTabHeader) {
                        if($this.cfg.scrollable && ($this.focusedTabHeader.index() === ($this.getLength() - 1))) {
                            break;
                        }
                        
                        if($this.focusedTabHeader.index() === ($this.getLength() - 1)) {
                            $this.focusedTabHeader = $this.navContainer.children('li:not(.ui-state-disabled):first');
                        }
                        else {
                            $this.focusedTabHeader = $this.focusedTabHeader.nextAll('li:not(.ui-state-disabled):first');
                            if(!$this.focusedTabHeader.length) {
                                $this.focusedTabHeader = $this.navContainer.children('li:not(.ui-state-disabled):first');
                            }
                        } 
                        $this.focusedTabHeader.trigger('focus.tabview');

                        if($this.cfg.scrollable) {
                            var rightScroll = $this.focusedTabHeader.position().left + $this.focusedTabHeader.width() > $this.navcrollerRight.position().left;
                            if(rightScroll) {
                                $this.navcrollerRight.trigger('click.tabview');
                            }
                        }
                    }
                    e.preventDefault();
                    clearTimeout($this.activating);
                    
                    $this.activating = setTimeout(function() {
                        $this.focusedTabHeader.trigger('click');
                    }, 500);
                break;
            }       
        });
    },
        
    initScrolling: function() {
        if(this.panelContainer.children().length) {
            var overflown = ((this.lastTab.position().left + this.lastTab.width()) - this.firstTab.position().left) > this.navscroller.innerWidth();
            if(overflown) {
                this.navscroller.css('padding-left', '18px');
                this.navcrollerLeft.show();
                this.navcrollerRight.show();
                this.restoreScrollState();
            }
        }
    },
        
    scroll: function(step) {
        if(this.navContainer.is(':animated')) {
            return;
        }
        
        var oldMarginLeft = parseInt(this.navContainer.css('margin-left')),
        newMarginLeft = oldMarginLeft + step,
        viewportWidth = this.navscroller.innerWidth(),
        $this = this;

        if(step < 0) {
            var lastTabBoundry = this.lastTab.position().left + parseInt(this.lastTab.innerWidth());
            
            if(lastTabBoundry > viewportWidth)
                this.navContainer.animate({'margin-left': newMarginLeft + 'px'}, 'fast', 'easeInOutCirc', function() {
                    $this.saveScrollState(newMarginLeft);
                    
                    if((lastTabBoundry + step) < viewportWidth)
                        $this.disableScrollerButton($this.navcrollerRight);
                    if($this.navcrollerLeft.hasClass('ui-state-disabled'))
                        $this.enableScrollerButton($this.navcrollerLeft);
                });
        }
        else {
            if(newMarginLeft <= 0) {
                this.navContainer.animate({'margin-left': newMarginLeft + 'px'}, 'fast', 'easeInOutCirc', function() {
                    $this.saveScrollState(newMarginLeft);
                    
                    if(newMarginLeft === 0)
                        $this.disableScrollerButton($this.navcrollerLeft);
                    if($this.navcrollerRight.hasClass('ui-state-disabled'))
                        $this.enableScrollerButton($this.navcrollerRight);
                });        
            }           
        }
    },
    
    disableScrollerButton: function(btn) {
        btn.addClass('ui-state-disabled').removeClass('ui-state-hover ui-state-active');
    },
            
    enableScrollerButton: function(btn) {
        btn.removeClass('ui-state-disabled');
    },
            
    saveScrollState: function(value) {
        this.scrollStateHolder.val(value);
    },
            
    restoreScrollState: function() {
        var value = parseInt(this.scrollStateHolder.val());
        if(value === 0) {
            this.disableScrollerButton(this.navcrollerLeft);
        }
        
        this.navContainer.css('margin-left', this.scrollStateHolder.val() + 'px');
    },
             
    /**
     * Selects an inactive tab given index
     */
    select: function(index, silent) {
        //Call user onTabChange callback
        if(this.cfg.onTabChange && !silent) {
            var result = this.cfg.onTabChange.call(this, index);
            if(result === false)
                return false;
        }

        var newPanel = this.panelContainer.children().eq(index),
        shouldLoad = this.cfg.dynamic && !this.isLoaded(newPanel);

        //update state
        this.stateHolder.val(index);
        this.cfg.selected = index;

        if(shouldLoad) {
            this.loadDynamicTab(newPanel);
        }
        else {
            this.show(newPanel);
            
            if(this.hasBehavior('tabChange') && !silent) {
                this.fireTabChangeEvent(newPanel);
            }
        }

        return true;
    },
    
    show: function(newPanel) {
        var headers = this.navContainer.children(),
        oldHeader = headers.filter('.ui-state-active'),
        newHeader = headers.eq(newPanel.index()),
        oldPanel = this.panelContainer.children('.ui-tabs-panel:visible'),
        _self = this;

        //aria
        oldPanel.attr('aria-hidden', true);
        oldHeader.attr('aria-expanded', false);
        oldHeader.attr('aria-selected', false);
        newPanel.attr('aria-hidden', false);
        newHeader.attr('aria-expanded', true);
        newHeader.attr('aria-selected', true);

        if(this.cfg.effect) {
                oldPanel.hide(this.cfg.effect, null, this.cfg.effectDuration, function() {
                oldHeader.removeClass('ui-tabs-selected ui-state-active');

                newHeader.addClass('ui-tabs-selected ui-state-active');
                newPanel.show(_self.cfg.effect, null, _self.cfg.effectDuration, function() {
                    _self.postTabShow(newPanel);
                });
            });
        }
        else {
            oldHeader.removeClass('ui-tabs-selected ui-state-active');
            oldPanel.hide();

            newHeader.addClass('ui-tabs-selected ui-state-active');
            newPanel.show();

            this.postTabShow(newPanel);
        }
    },
    
    /**
     * Loads tab contents with ajax
     */
    loadDynamicTab: function(newPanel) {
        var $this = this,
        tabindex = newPanel.index(),
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true},
                {name: this.id + '_newTab', value: newPanel.attr('id')},
                {name: this.id + '_tabindex', value: tabindex}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            newPanel.html(content);

                            if(this.cfg.cache) {
                                this.markAsLoaded(newPanel);
                            }
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.show(newPanel);
            }
        };

        if(this.hasBehavior('tabChange')) {
            var tabChangeBehavior = this.cfg.behaviors['tabChange'];

            tabChangeBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
    
    /**
     * Removes a tab with given index
     */
    remove: function(index) {    
        var header = this.navContainer.children().eq(index),
        panel = this.panelContainer.children().eq(index);

        header.remove();
        panel.remove();
        
        var length = this.getLength();
        
        if(length > 0) {
            if(index < this.cfg.selected) {
                this.cfg.selected--;
            }
            else if(index === this.cfg.selected) {
                var newIndex = (this.cfg.selected === (length)) ? (this.cfg.selected - 1): this.cfg.selected;
                this.select(newIndex, true);
            }
        }
        else {
            this.cfg.selected = -1;
        }
        
        this.fireTabCloseEvent(panel.attr('id'), index);
    },
    
    getLength: function() {
        return this.navContainer.children().length;
    },
    
    getActiveIndex: function() {
        return this.cfg.selected;
    },
    
    fireTabChangeEvent: function(panel) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: [
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: panel.index()}
            ]
        };
        
        tabChangeBehavior.call(this, ext);
    },
    
    fireTabCloseEvent: function(id, index) {    
        if(this.hasBehavior('tabClose')) {
            var tabCloseBehavior = this.cfg.behaviors['tabClose'],
            ext = {
                params: [
                    {name: this.id + '_closeTab', value: id},
                    {name: this.id + '_tabindex', value: index}
                ]
            };

            tabCloseBehavior.call(this, ext);
        }
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }

        return false;
    },
    
    markAsLoaded: function(panel) {
        panel.data('loaded', true);
    },
    
    isLoaded: function(panel) {
        return panel.data('loaded') === true;
    },
    
    disable: function(index) {
        this.navContainer.children().eq(index).addClass('ui-state-disabled');
    },
    
    enable: function(index) {
        this.navContainer.children().eq(index).removeClass('ui-state-disabled');
    },
    
    postTabShow: function(newPanel) {    
        //execute user defined callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel.index());
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    }

});
/**
 * PrimeFaces TagCloud Widget
 */
PrimeFaces.widget.TagCloud = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        var _self = this;
        
        this.jq.find('a').mouseover(function() {
            $(this).addClass('ui-state-hover');
        })
        .mouseout(function() {
            $(this).removeClass('ui-state-hover');
        })
        .click(function(e) {
            var link = $(this);
            
            if(link.attr('href') === '#') {
                _self.fireSelectEvent(link);
                e.preventDefault();
            }
        });
    },
    
    fireSelectEvent: function(link) {
        if(this.cfg.behaviors) {
            var selectBehavior = this.cfg.behaviors['select'];

            if(selectBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_itemIndex', value: link.parent().index()}
                    ]
                };
                
                selectBehavior.call(this, ext);
            }
        }
    }
    
});
/**
 * PrimeFaces Tooltip Widget
 */
PrimeFaces.widget.Tooltip = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.cfg.showEvent = this.cfg.showEvent ? this.cfg.showEvent + '.tooltip' : 'mouseover.tooltip';
        this.cfg.hideEvent = this.cfg.hideEvent ? this.cfg.hideEvent + '.tooltip' : 'mouseout.tooltip';
        this.cfg.showEffect = this.cfg.showEffect ? this.cfg.showEffect : 'fade';
        this.cfg.hideEffect = this.cfg.hideEffect ? this.cfg.hideEffect : 'fade';
        this.cfg.showDelay = this.cfg.showDelay||150;
        this.cfg.hideDelay = this.cfg.hideDelay||0;
        this.cfg.hideEffectDuration = this.cfg.target ? 250 : 1;
        
        if(this.cfg.target)
            this.bindTarget();
        else
            this.bindGlobal();
        
        this.removeScriptElement(this.id);
    },
    
    refresh: function(cfg) {
        if(cfg.target) {
            if($(PrimeFaces.escapeClientId(cfg.id)).length > 1) 
                $(document.body).children(PrimeFaces.escapeClientId(cfg.id)).remove();
        }
        else {
            $(document.body).children('.ui-tooltip-global').remove();
        }
        
        this._super(cfg);
    },
    
    bindGlobal: function() {
        this.jq = $('<div class="ui-tooltip ui-tooltip-global ui-widget ui-widget-content ui-corner-all ui-shadow" />').appendTo('body');
        this.cfg.globalSelector = this.cfg.globalSelector||'a,:input,:button';
        this.cfg.escape = (this.cfg.escape === undefined) ? true : this.cfg.escape;
        var $this = this;
        
        $(document).off(this.cfg.showEvent + ' ' + this.cfg.hideEvent, this.cfg.globalSelector)
                    .on(this.cfg.showEvent, this.cfg.globalSelector, function(e) {
                        var element = $(this);
                        if(element.prop('disabled')) {
                            return;
                        }
                        
                        if($this.cfg.trackMouse) {
                            $this.mouseEvent = e;
                        }
                
                        var title = element.attr('title');
                        if(title) {
                            element.data('tooltip', title).removeAttr('title');
                        }
                        
                        if(element.hasClass('ui-state-error')) {
                            $this.jq.addClass('ui-state-error');
                        }

                        var text = element.data('tooltip');
                        if(text) {
                            if($this.cfg.escape)
                                $this.jq.text(text);
                            else
                                $this.jq.html(text);
                                
                            $this.globalTitle = text;
                            $this.target = element;
                            $this.show();
                        }
                    })
                    .on(this.cfg.hideEvent + '.tooltip', this.cfg.globalSelector, function() {
                        if($this.globalTitle) {
                            $this.hide();
                            $this.globalTitle = null;
                            $this.target = null;
                            $this.jq.removeClass('ui-state-error');
                        }
                    });
                    
        var resizeNS = 'resize.tooltip';
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.jq.is(':visible')) {
                $this.align();
            }
        });
    },
    
    bindTarget: function() {
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jq = $(this.jqId);
        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);
        
        var $this = this;
        this.target.off(this.cfg.showEvent + ' ' + this.cfg.hideEvent)
                    .on(this.cfg.showEvent, function(e) {
                        if($this.cfg.trackMouse) {
                            $this.mouseEvent = e;
                        }
                        
                        var text = $.trim($this.jq.text());
                        if($this.jq.children().length > 0 || text !== '') {
                            $this.show();
                        }
                    })
                    .on(this.cfg.hideEvent + '.tooltip', function() {
                        $this.hide();
                    });

        this.jq.appendTo(document.body);

        if($.trim(this.jq.html()) === '') {
            this.jq.html(this.target.attr('title'));
        }

        this.target.removeAttr('title');
        
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.jq.is(':visible')) {
                $this.align();
            }
        });
    },

    align: function() {
         this.jq.css({
            left:'', 
            top:'',
            'z-index': ++PrimeFaces.zindex
        });
        
        if(this.cfg.trackMouse && this.mouseEvent) {
            this.jq.position({
                my: 'left top+15',
                at: 'right bottom',
                of: this.mouseEvent,
                collision: 'flipfit'
            });
            
            this.mouseEvent = null;
        }
        else {
            this.jq.position({
                my: 'left top',
                at: 'right bottom',
                of: this.target,
                collision: 'flipfit'
            });
        }
    },
    
    show: function() {
        if(this.target) {
            var $this = this;
            this.clearTimeout();

            this.timeout = setTimeout(function() {
                $this._show();
            }, this.cfg.showDelay);
        }
    },
    
    _show: function() {
        var $this = this;
        
        if(this.cfg.beforeShow) {
            var retVal = this.cfg.beforeShow.call(this);
            if(retVal === false) {
                return;
            }
        }
        
        this.align();
        if(this.cfg.trackMouse) {
            this.followMouse();
        }
        this.jq.show(this.cfg.showEffect, {}, 250, function() {
            if($this.cfg.onShow) {
                $this.cfg.onShow.call();
            }
        });
    },
    
    hide: function() {
        var $this = this;
        this.clearTimeout();

        if(this.cfg.hideDelay) {
            this.timeout = setTimeout(function() {
                $this._hide();
            }, this.cfg.hideDelay);
        }
        else {
            this._hide();
        }
    },
    
    _hide: function() {
        var $this = this;
        
        if(this.isVisible()) {
            this.jq.hide(this.cfg.hideEffect, {}, this.cfg.hideEffectDuration, function() {
                $(this).css('z-index', '');
                if($this.cfg.trackMouse) {
                    $this.unfollowMouse();
                }

                if($this.cfg.onHide) {
                    $this.cfg.onHide.call();
                }
            });
        }
    },
    
    clearTimeout: function() {
        if(this.timeout) {
            clearTimeout(this.timeout);
        }
    },
    
    followMouse: function() {
        var $this = this;
        
        this.target.on('mousemove.tooltip-track', function(e) {
            $this.jq.position({
                my: 'left top+15',
                at: 'right bottom',
                of: e,
                collision: 'flipfit'
            });
        });       
    },
    
    unfollowMouse: function() {
        this.target.off('mousemove.tooltip-track'); 
    },
    
    isVisible: function() {
        return this.jq.is(':visible');
    }
    
});
/**
 * PrimeFaces Base Tree Widget
 */
PrimeFaces.widget.BaseTree = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.cfg.highlight = (this.cfg.highlight === false) ? false : true;
        this.focusedNode = null;

        if(this.cfg.selectionMode) {
            this.initSelection();
        }

        this.bindEvents();
        
        this.jq.data('widget', this);
    },
    
    initSelection: function() {
        this.selectionHolder = $(this.jqId + '_selection');
        var selectionsValue = this.selectionHolder.val();
        this.selections = selectionsValue === '' ? [] : selectionsValue.split(',');

        if(this.isCheckboxSelection()) {
            this.preselectCheckbox();
        }
    },
    
    expandNode: function(node) {    
        var $this = this;

        if(this.cfg.dynamic) {
            if(this.cfg.cache && $this.getNodeChildrenContainer(node).children().length > 0) {
                this.showNodeChildren(node);

                return;
            }

            if(node.data('processing')) {
                PrimeFaces.debug('Node is already being expanded, ignoring expand event.');
                return;
            }

            node.data('processing', true);

            var options = {
                source: this.id,
                process: this.id,
                update: this.id,
                formId: this.cfg.formId,
                params: [
                    {name: this.id + '_expandNode', value: $this.getRowKey(node)}
                ],
                onsuccess: function(responseXML, status, xhr) {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                            widget: $this,
                            handle: function(content) {
                                var nodeChildrenContainer = this.getNodeChildrenContainer(node);
                                nodeChildrenContainer.append(content);

                                this.showNodeChildren(node);

                                if(this.cfg.draggable) {                            
                                    this.makeDraggable(nodeChildrenContainer.find('span.ui-treenode-content'));
                                }

                                if(this.cfg.droppable) {
                                    this.makeDropPoints(nodeChildrenContainer.find('li.ui-tree-droppoint'));
                                    this.makeDropNodes(nodeChildrenContainer.find('span.ui-treenode-droppable'));
                                }
                            }
                        });

                    return true;
                },
                oncomplete: function() {
                    node.removeData('processing');
                }
            };

            if(this.hasBehavior('expand')) {
                var expandBehavior = this.cfg.behaviors['expand'];
                expandBehavior.call(this, options);
            }
            else {
                PrimeFaces.ajax.Request.handle(options);
            }
        }
        else {
            this.showNodeChildren(node);
            this.fireExpandEvent(node);
        }
    },
    
    fireExpandEvent: function(node) {
        if(this.cfg.behaviors) {
            var expandBehavior = this.cfg.behaviors['expand'];
            if(expandBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_expandNode', value: this.getRowKey(node)}
                    ]
                };

                expandBehavior.call(this, ext);
            }
        }
    },
    
    fireCollapseEvent: function(node) {
        if(this.cfg.behaviors) {
            var collapseBehavior = this.cfg.behaviors['collapse'];
            if(collapseBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_collapseNode', value: this.getRowKey(node)}
                    ]
                };

                collapseBehavior.call(this, ext);
            }
        }
    },
    
    getNodeChildrenContainer: function(node) {
        throw "Unsupported Operation";
    },
    
    showNodeChildren: function(node) {
        throw "Unsupported Operation";
    },
    
    writeSelections: function() {   
        this.selectionHolder.val(this.selections.join(','));
    },
    
    fireNodeSelectEvent: function(node) {
        if(this.isCheckboxSelection() && this.cfg.dynamic) {
            var $this = this,
            options = {
                source: this.id,
                process: this.id
            };
            
            options.params = [
                {name: this.id + '_instantSelection', value: this.getRowKey(node)}
            ];
            
            options.oncomplete = function(xhr, status, args) {
                if(args.descendantRowKeys && args.descendantRowKeys !== '') {
                    var rowKeys = args.descendantRowKeys.split(',');
                    for(var i = 0; i < rowKeys.length; i++) {
                        $this.addToSelection(rowKeys[i]);
                    }
                    $this.writeSelections();
                }     
            }
            
            if(this.hasBehavior('select')) {
                var selectBehavior = this.cfg.behaviors['select'];
                selectBehavior.call(this, options);
            }
            else {
                PrimeFaces.ajax.AjaxRequest(options);
            }
        }
        else {
            if(this.hasBehavior('select')) {
                var selectBehavior = this.cfg.behaviors['select'],
                ext = {
                    params: [
                        {name: this.id + '_instantSelection', value: this.getRowKey(node)}
                    ]
                };
                
                selectBehavior.call(this, ext);
            }
        }
    },
    
    fireNodeUnselectEvent: function(node) {
        if(this.cfg.behaviors) {
            var unselectBehavior = this.cfg.behaviors['unselect'];

            if(unselectBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_instantUnselection', value: this.getRowKey(node)}
                    ]
                };

                unselectBehavior.call(this, ext);
            }
        }
    },
            
    fireContextMenuEvent: function(node) {
        if(this.hasBehavior('contextMenu')) {
            var contextMenuBehavior = this.cfg.behaviors['contextMenu'],
            ext = {
                params: [
                    {name: this.id + '_contextMenuNode', value: this.getRowKey(node)}
                ]
            };

            contextMenuBehavior.call(this, ext);
        }
    },
    
    getRowKey: function(node) {
        return node.attr('data-rowkey');
    },
    
    isNodeSelected: function(node) {
        return $.inArray(this.getRowKey(node), this.selections) != -1;
    },
    
    isSingleSelection: function() {
        return this.cfg.selectionMode == 'single';
    },
    
    isMultipleSelection: function() {
        return this.cfg.selectionMode == 'multiple';
    },
    
    isCheckboxSelection: function() {
        return this.cfg.selectionMode == 'checkbox';
    },
    
    addToSelection: function(rowKey) {
        if(!PrimeFaces.inArray(this.selections, rowKey)) {
            this.selections.push(rowKey);
        }
    },
    
    removeFromSelection: function(rowKey) {
        this.selections = $.grep(this.selections, function(r) {
            return r !== rowKey;
        });
    },
            
    removeDescendantsFromSelection: function(rowKey) {
        var newSelections = [];
        for(var i = 0; i < this.selections.length; i++) {
            if(this.selections[i].indexOf(rowKey + '_') !== 0)
                newSelections.push(this.selections[i]);
        }
        this.selections = newSelections;
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    },
       
    nodeClick: function(event, nodeContent) {
        PrimeFaces.clearSelection();
        
        if($(event.target).is(':not(.ui-tree-toggler)')) {
            var node = nodeContent.parent(),
            selectable = nodeContent.hasClass('ui-tree-selectable');
                    
            if(this.cfg.onNodeClick) {
                this.cfg.onNodeClick.call(this, node, event);
            }
            
            if(selectable && (this.cfg.selectionMode||this.cfg.draggable)) {
                var selected = this.isNodeSelected(node),
                metaKey = event.metaKey||event.ctrlKey,
                shiftKey = event.shiftKey;
                
                if(this.isCheckboxSelection()) {
                    this.toggleCheckboxNode(node);
                }
                else {
                    if(selected && (metaKey)) {
                        this.unselectNode(node);
                    }
                    else {
                        if(this.isSingleSelection()||(this.isMultipleSelection() && !metaKey)) {
                            this.unselectAllNodes();
                        }

                        this.selectNode(node);
                        this.cursorNode = node;
                    }
                }

                if($(event.target).is(':not(:input:enabled)')) {
                    this.focusNode(node);
                }
            }
        }
    },
            
    nodeRightClick: function(event, nodeContent) {
        PrimeFaces.clearSelection();
        
        if($(event.target).is(':not(.ui-tree-toggler)')) {
            var node = nodeContent.parent(),
            selectable = nodeContent.hasClass('ui-tree-selectable');
    
            if(selectable && this.cfg.selectionMode) {
                var selected = this.isNodeSelected(node);
                if(!selected) {
                    if(this.isCheckboxSelection()) {
                        this.toggleCheckboxNode(node);
                    }
                    else {
                        this.unselectAllNodes();                        
                        this.selectNode(node, true);
                    }
                }
                
                this.fireContextMenuEvent(node);
            }
        }
    },
    
    bindEvents: function() {
        throw "Unsupported Operation";
    },
    
    selectNode: function(node, silent) {        
        throw "Unsupported Operation";
    },
    
    unselectNode: function(node, silent) {        
        throw "Unsupported Operation";
    },
    
    unselectAllNodes: function() {        
        throw "Unsupported Operation";
    },
    
    preselectCheckbox: function() {
        throw "Unsupported Operation";
    },
    
    toggleCheckboxNode: function(node) {
        throw "Unsupported Operation";
    },
            
    isEmpty: function() {
        throw "Unsupported Operation";
    },
    
    toggleCheckboxState: function(checkbox, checked) {
        if(checked)
            this.uncheck(checkbox);
        else
            this.check(checkbox);
    },
    
    partialCheck: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        treeNode.find('> .ui-treenode-content > .ui-treenode-label').removeClass('ui-state-highlight');
        icon.removeClass('ui-icon-blank ui-icon-check').addClass('ui-icon-minus');
        treeNode.removeClass('ui-treenode-selected ui-treenode-unselected').addClass('ui-treenode-hasselected').attr('aria-checked', false).attr('aria-selected', false);
    
        this.removeFromSelection(rowKey);
    },
        
    check: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        box.removeClass('ui-state-hover');
        icon.removeClass('ui-icon-blank ui-icon-minus').addClass('ui-icon-check');
        treeNode.removeClass('ui-treenode-hasselected ui-treenode-unselected').addClass('ui-treenode-selected').attr('aria-checked', true).attr('aria-selected', true);
        
        this.addToSelection(rowKey);
    },
    
    uncheck: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);
        
        box.removeClass('ui-state-hover');
        icon.removeClass('ui-icon-minus ui-icon-check').addClass('ui-icon-blank');
        treeNode.removeClass('ui-treenode-hasselected ui-treenode-selected').addClass('ui-treenode-unselected').attr('aria-checked', false).attr('aria-selected', false);
        
        this.removeFromSelection(rowKey);
    },
        
    isExpanded: function(node) {
        return this.getNodeChildrenContainer(node).is(':visible');
    },
    
    focusNode: function() {
        throw "Unsupported Operation";
    }
    
});

/**
 * PrimeFaces Vertical Tree Widget
 */
PrimeFaces.widget.VerticalTree = PrimeFaces.widget.BaseTree.extend({
        
    init: function(cfg) {
        this._super(cfg);
    
        this.container = this.jq.children('.ui-tree-container');
        this.cfg.rtl = this.jq.hasClass('ui-tree-rtl');
        this.cfg.collapsedIcon = this.cfg.rtl ? 'ui-icon-triangle-1-w' : 'ui-icon-triangle-1-e';
        
        if(this.cfg.draggable) {
            this.initDraggable();
        }
        
        if(this.cfg.droppable) {
            this.initDroppable();
        }
    },
        
    bindEvents: function() {
        var $this = this,
        togglerSelector = '.ui-tree-toggler',
        nodeLabelSelector = '.ui-tree-selectable .ui-treenode-label',
        nodeContentSelector = '.ui-treenode-content';

        this.jq.off('click.tree-toggle', togglerSelector)
                    .on('click.tree-toggle', togglerSelector, null, function(e) {
                        var toggleIcon = $(this),
                        node = toggleIcon.closest('li');

                        if(toggleIcon.hasClass($this.cfg.collapsedIcon))
                            $this.expandNode(node);
                        else
                            $this.collapseNode(node);
                    });

        if(this.cfg.highlight && this.cfg.selectionMode) {
            this.jq.off('mouseout.tree mouseover.tree', nodeLabelSelector)
                        .on('mouseout.tree', nodeLabelSelector, null, function() {
                            var label = $(this);
                            
                            label.removeClass('ui-state-hover');
                            
                            if($this.isCheckboxSelection()) {
                                label.siblings('div.ui-chkbox').children('div.ui-chkbox-box').removeClass('ui-state-hover');
                            }
                        })
                        .on('mouseover.tree', nodeLabelSelector, null, function() {
                            var label = $(this);

                            $(this).addClass('ui-state-hover');
                                
                            if($this.isCheckboxSelection()) {
                                label.siblings('div.ui-chkbox').children('div.ui-chkbox-box').addClass('ui-state-hover');
                            }
                        });
        }
        
        if(this.isCheckboxSelection()) {       
            var checkboxSelector = '.ui-chkbox-box:not(.ui-state-disabled)';
            
            this.jq.off('mouseout.tree-checkbox mouseover.tree-checkbox click.tree-checkbox', checkboxSelector)
                        .on('mouseout.tree-checkbox', checkboxSelector, null, function() {
                            $(this).removeClass('ui-state-hover').parent().siblings('span.ui-treenode-label').removeClass('ui-state-hover');
                        })
                        .on('mouseover.tree-checkbox', checkboxSelector, null, function() {
                            $(this).addClass('ui-state-hover').parent().siblings('span.ui-treenode-label').addClass('ui-state-hover');
                        });
        }
        
        this.jq.off('click.tree-content', nodeContentSelector)
                        .on('click.tree-content', nodeContentSelector, null, function(e) {
                            $this.nodeClick(e, $(this));
                        });
                        
        this.bindKeyEvents();                
    },
            
    bindKeyEvents: function() {
        var $this = this;

        this.jq.on('mousedown.tree', function(e) {
            if($(e.target).is(':not(:input:enabled)')) {
                e.preventDefault();
            }
        })
        .on('focus.tree', function() {
            if(!$this.focusedNode) {
                $this.focusNode($this.getFirstNode());
            }
        });
        
        this.jq.off('keydown.tree blur.tree', '.ui-treenode-label').on('keydown.tree', '.ui-treenode-label', null, function(e) {
            if(!$this.focusedNode) {
                return;
            }
            
            var searchRowkey = "",
            keyCode = $.ui.keyCode;
            
            switch(e.which) {
                case keyCode.LEFT:
                    var rowkey = $this.focusedNode.data('rowkey').toString(),
                    keyLength = rowkey.length;

                    if($this.isExpanded($this.focusedNode)) {
                        $this.collapseNode($this.focusedNode);
                    }
                    else {
                        var nodeToFocus = null;
                        for(var i = 1; i < parseInt(keyLength / 2) + 1; i++){
                            searchRowkey = rowkey.substring(0, keyLength - 2 * i);
                            nodeToFocus = $this.container.find("li:visible[data-rowkey = '" + searchRowkey + "']");
                            if(nodeToFocus.length) {
                                $this.focusNode(nodeToFocus);
                                break;
                            }
                        }
                    }
                        
                    e.preventDefault();
                break;

                case keyCode.RIGHT:
                    if(!$this.focusedNode.hasClass('ui-treenode-leaf')) {
                        var rowkey = $this.focusedNode.data('rowkey').toString(),
                        keyLength = rowkey.length;

                        if(!$this.isExpanded($this.focusedNode)) {
                            $this.expandNode($this.focusedNode);
                        }
                        
                        if(!$this.isExpanded($this.focusedNode) && !$this.cfg.dynamic) {
                            searchRowkey = rowkey + '_0';
                            var nodeToFocus = $this.container.find("li:visible[data-rowkey = '" + searchRowkey + "']");

                            if(nodeToFocus.length) {
                                $this.focusNode(nodeToFocus);
                            }           
                        }
                    }
                    
                    e.preventDefault();
                break;

                case keyCode.UP:
                    var nodeToFocus = null,
                    prevNode = $this.focusedNode.prev();
                    
                    if(prevNode.length) {
                        nodeToFocus = prevNode.find('li.ui-treenode:visible:last');
                        if(!nodeToFocus.length) {
                            nodeToFocus = prevNode;    
                        }
                    }
                    else {
                        nodeToFocus = $this.focusedNode.closest('ul').parent('li');
                    }

                    if(nodeToFocus.length) {
                        $this.focusNode(nodeToFocus);
                    }
                        
                    e.preventDefault();
                break;

                case keyCode.DOWN:
                    var nodeToFocus = null,
                    firstVisibleChildNode = $this.focusedNode.find("> ul > li:visible:first");
                    
                    if(firstVisibleChildNode.length) {
                        nodeToFocus = firstVisibleChildNode;
                    }
                    else if($this.focusedNode.next().length) {
                        nodeToFocus = $this.focusedNode.next();
                    }
                    else {
                        var rowkey = $this.focusedNode.data('rowkey').toString();
                        
                        if(rowkey.length !== 1) {
                            nodeToFocus = $this.searchDown($this.focusedNode);
                        }
                    }

                    if(nodeToFocus && nodeToFocus.length) {
                        $this.focusNode(nodeToFocus);
                    }
                        
                    e.preventDefault();
                break;
                
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                case keyCode.SPACE:
                    if($this.cfg.selectionMode) {
                        var selectable = $this.focusedNode.children('.ui-treenode-content').hasClass('ui-tree-selectable');
                        
                        if($this.cfg.onNodeClick) {
                            $this.cfg.onNodeClick.call($this, $this.focusedNode, e);
                        }
                        
                        if(selectable) {
                            var selected = $this.isNodeSelected($this.focusedNode);

                            if($this.isCheckboxSelection()) {
                                $this.toggleCheckboxNode($this.focusedNode);
                            }
                            else {
                                if(selected) {
                                    $this.unselectNode($this.focusedNode);
                                }
                                else {
                                    if($this.isSingleSelection()) {
                                        $this.unselectAllNodes();
                                    }
                                    
                                    $this.selectNode($this.focusedNode);
                                    $this.cursorNode = $this.focusedNode;
                                }
                            }
                        }
                    }

                    e.preventDefault();
                break;
            }       
        })
        .on('blur.tree', '.ui-treenode-label', null, function(e) {
            if($this.focusedNode) {
                $this.getNodeLabel($this.focusedNode).removeClass('ui-treenode-outline');
                $this.focusedNode = null;
            }
        });
    },
    
    searchDown: function(node) {
        var nextOfParent = node.closest('ul').parent('li').next(),
        nodeToFocus = null;
        
        if(nextOfParent.length) {
            nodeToFocus = nextOfParent;
        }
        else if(node.hasClass('ui-treenode-leaf') && node.closest('ul').parent('li').length == 0){   
            nodeToFocus = node;
        }
        else {
            var rowkey = node.data('rowkey').toString();
            
            if(rowkey.length !== 1) {
                nodeToFocus = this.searchDown(node.closest('ul').parent('li'));
            }
        }
        
        return nodeToFocus;
    },        
            
    collapseNode: function(node) {
        var _self = this,
        nodeContent = node.find('> .ui-treenode-content'), 
        toggleIcon = nodeContent.find('> .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        nodeIcon = toggleIcon.nextAll('span.ui-treenode-icon'),
        iconState = this.cfg.iconStates[nodeType],
        childrenContainer = node.children('.ui-treenode-children');
        
        //aria
        nodeContent.find('> .ui-treenode-label').attr('aria-expanded', false);
        
        toggleIcon.addClass(_self.cfg.collapsedIcon).removeClass('ui-icon-triangle-1-s');
        
        if(iconState) {
            nodeIcon.removeClass(iconState.expandedIcon).addClass(iconState.collapsedIcon);
        }
        
        if(this.cfg.animate) {
            childrenContainer.slideUp('fast', function() {
                _self.postCollapse(node, childrenContainer);
            });
        }
        else {
            childrenContainer.hide();
            this.postCollapse(node, childrenContainer);
        }
    },

    postCollapse: function(node, childrenContainer) {
        if(this.cfg.dynamic && !this.cfg.cache) {
            childrenContainer.empty();
        }
        
        this.fireCollapseEvent(node);
    },
        
    //@Override
    getNodeChildrenContainer: function(node) {
        return node.children('.ui-treenode-children');
    },
    
    //@Override
    showNodeChildren: function(node) {
        var nodeContent = node.find('> .ui-treenode-content'), 
        toggleIcon = nodeContent.find('> .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        nodeIcon = toggleIcon.nextAll('span.ui-treenode-icon'),
        iconState = this.cfg.iconStates[nodeType];

        //aria
        nodeContent.find('> .ui-treenode-label').attr('aria-expanded', true);
        
        toggleIcon.addClass('ui-icon-triangle-1-s').removeClass(this.cfg.collapsedIcon);

        if(iconState) {
            nodeIcon.removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
        }

        if(this.cfg.animate) {
            node.children('.ui-treenode-children').slideDown('fast');
        }
        else {
            node.children('.ui-treenode-children').show();
        }
    },
    
    unselectAllNodes: function() {
        this.selections = [];
        this.jq.find('.ui-treenode-label.ui-state-highlight').each(function() {
            $(this).removeClass('ui-state-highlight').closest('.ui-treenode').attr('aria-selected', false);
        });
    },
    
    selectNode: function(node, silent) {        
        node.attr('aria-selected', true)
            .find('> .ui-treenode-content > .ui-treenode-label').removeClass('ui-state-hover').addClass('ui-state-highlight');

        this.addToSelection(this.getRowKey(node));
        this.writeSelections(); 
        
        if(!silent)
            this.fireNodeSelectEvent(node);
    },
    
    unselectNode: function(node, silent) {
        var rowKey = this.getRowKey(node);
           
        node.attr('aria-selected', false).
            find('> .ui-treenode-content > .ui-treenode-label').removeClass('ui-state-highlight ui-state-hover');

        this.removeFromSelection(rowKey);
        this.writeSelections();

        if(!silent)
            this.fireNodeUnselectEvent(node);
    },

    toggleCheckboxNode: function(node) {
        var $this = this,
        checkbox = node.find('> .ui-treenode-content > .ui-chkbox'),
        checked = checkbox.find('> .ui-chkbox-box > .ui-chkbox-icon').hasClass('ui-icon-check');

        this.toggleCheckboxState(checkbox, checked);

        if(this.cfg.propagateDown) {
            node.children('.ui-treenode-children').find('.ui-chkbox').each(function() {
                $this.toggleCheckboxState($(this), checked);
            });
            
            if(this.cfg.dynamic) {
                this.removeDescendantsFromSelection(node.data('rowkey'));
            }
        }

        if(this.cfg.propagateUp) {
            node.parents('li.ui-treenode-parent').each(function() {
                var parentNode = $(this),
                parentsCheckbox = parentNode.find('> .ui-treenode-content > .ui-chkbox'),
                children = parentNode.find('> .ui-treenode-children > .ui-treenode');
                
                if(checked) {
                    if(children.filter('.ui-treenode-unselected').length === children.length)
                        $this.uncheck(parentsCheckbox);
                    else
                        $this.partialCheck(parentsCheckbox);
                }
                else {
                    if(children.filter('.ui-treenode-selected').length === children.length)
                        $this.check(parentsCheckbox);
                    else
                        $this.partialCheck(parentsCheckbox);
                }
            });
        }
        
        this.writeSelections();

        if(checked)
            this.fireNodeUnselectEvent(node);
        else
            this.fireNodeSelectEvent(node);
    },
          
    preselectCheckbox: function() {
        this.jq.find('.ui-chkbox-icon').not('.ui-icon-check').each(function() {
            var icon = $(this),
            node = icon.closest('li');

            if(node.children('.ui-treenode-children').find('.ui-chkbox-icon.ui-icon-check').length > 0) {
                node.addClass('ui-treenode-hasselected');
                icon.removeClass('ui-icon-blank').addClass('ui-icon-minus');
            }
        });
    },
            
    check: function(checkbox) {
        this._super(checkbox);
        checkbox.siblings('span.ui-treenode-label').addClass('ui-state-highlight').removeClass('ui-state-hover');
    },
            
    uncheck: function(checkbox) {
        this._super(checkbox);
        checkbox.siblings('span.ui-treenode-label').removeClass('ui-state-highlight');
    },
    
    initDraggable: function() {
        this.makeDraggable(this.jq.find('span.ui-treenode-content'));
    },
            
    initDroppable: function() {
        this.makeDropPoints(this.jq.find('li.ui-tree-droppoint'));
        this.makeDropNodes(this.jq.find('span.ui-treenode-droppable'));
        this.initDropScrollers();
    },
    
    makeDraggable: function(elements) {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;
        
        elements.draggable({
            helper: function() {
                var el = $('<div class="ui-tree-draghelper ui-state-highlight"></div>');
                el.width($this.jq.width());
                el.height(20);
                
                return el;
            },
            appendTo: document.body,
            zIndex: ++PrimeFaces.zindex,
            revert: true,
            scope: dragdropScope
        })
        .data({
            'dragsourceid': this.jqId,
            'dragmode': this.cfg.dragMode
        });
    },
            
    makeDropPoints: function(elements) {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;

        elements.droppable({
            hoverClass: 'ui-state-hover',
            accept: 'span.ui-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            drop: function(event, ui) {
                var dragSource = $(ui.draggable.data('dragsourceid')).data('widget'),
                dragMode = ui.draggable.data('dragmode'),
                dropSource = $this,
                dropPoint = $(this),
                dropNode = dropPoint.closest('li.ui-treenode-parent'),
                dropNodeKey = $this.getRowKey(dropNode),
                dragNode = ui.draggable.closest('li.ui-treenode'),
                targetDragNode = $this.findTargetDragNode(dragNode, dragMode),
                dragNodeKey = $this.getRowKey(targetDragNode),
                dragNodeDropPoint = targetDragNode.next('li.ui-tree-droppoint'),
                oldParentNode = targetDragNode.parent().closest('li.ui-treenode-parent'),
                transfer = (dragSource.id !== dropSource.id);
                
                ui.helper.remove();
                dropPoint.removeClass('ui-state-hover');
                
                var validDrop = $this.validateDropPoint(dragNode, dropPoint);
                if(!validDrop) {
                    return;
                }

                targetDragNode.hide().insertAfter(dropPoint);

                if(transfer) {
                    if(dragSource.cfg.selectionMode) {
                        dragSource.unselectSubtree(targetDragNode);
                    }

                    dragNodeDropPoint.remove();
                    $this.updateDragDropBindings(targetDragNode);
                }
                else {
                    dragNodeDropPoint.insertAfter(targetDragNode);
                }

                if(oldParentNode.length && (oldParentNode.find('> ul.ui-treenode-children > li.ui-treenode').length === 0)) {
                    $this.makeLeaf(oldParentNode);
                }

                targetDragNode.fadeIn();
                
                if($this.isCheckboxSelection()) {                                                
                    $this.syncDNDCheckboxes(dragSource, oldParentNode, dropNode);
                }
                
                $this.syncDragDrop();
                if(transfer) {
                    dragSource.syncDragDrop();
                }
                
                $this.fireDragDropEvent({
                    'dragNodeKey': dragNodeKey,
                    'dropNodeKey': dropNodeKey,
                    'dragSource': dragSource.id,
                    'dndIndex': dropPoint.prevAll('li.ui-treenode').length,
                    'transfer': transfer
                });
            }
        });
    },
        
    makeDropNodes: function(elements) {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;
        
        elements.droppable({
            accept: '.ui-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            over: function(event, ui) {
                $(this).children('.ui-treenode-label').addClass('ui-state-hover');
            },
            out: function(event, ui) {
                $(this).children('.ui-treenode-label').removeClass('ui-state-hover');
            },
            drop: function(event, ui) {
                var dragSource = $(ui.draggable.data('dragsourceid')).data('widget'),
                dragMode = ui.draggable.data('dragmode'),
                dropSource = $this,
                droppable = $(this),
                dropNode = droppable.closest('li.ui-treenode'),
                dropNodeKey = $this.getRowKey(dropNode),
                dragNode = ui.draggable.closest('li.ui-treenode'),
                targetDragNode = $this.findTargetDragNode(dragNode, dragMode),
                dragNodeKey = $this.getRowKey(targetDragNode),
                dragNodeDropPoint = targetDragNode.next('li.ui-tree-droppoint'),
                oldParentNode = targetDragNode.parent().closest('li.ui-treenode-parent'),
                childrenContainer = dropNode.children('.ui-treenode-children'),
                transfer = (dragSource.id !== dropSource.id);

                ui.helper.remove();
                droppable.children('.ui-treenode-label').removeClass('ui-state-hover');
                
                var validDrop = $this.validateDropNode(dragNode, dropNode, oldParentNode);  
                if(!validDrop) {
                    return;
                }
                
                if(childrenContainer.children('li.ui-treenode').length === 0) {
                    $this.makeParent(dropNode);
                }

                targetDragNode.hide();
                childrenContainer.append(targetDragNode);

                if(oldParentNode.length && (oldParentNode.find('> ul.ui-treenode-children > li.ui-treenode').length === 0)) {
                    $this.makeLeaf(oldParentNode);
                }

                if(transfer) {
                    if(dragSource.cfg.selectionMode) {
                        dragSource.unselectSubtree(targetDragNode);
                    }

                    dragNodeDropPoint.remove();
                    $this.updateDragDropBindings(targetDragNode);
                }
                else {
                    childrenContainer.append(dragNodeDropPoint);
                }

                targetDragNode.fadeIn();
                
                if($this.isCheckboxSelection()) {                                                
                    $this.syncDNDCheckboxes(dragSource, oldParentNode, dropNode);
                }
                
                $this.syncDragDrop();
                if(transfer) {
                    dragSource.syncDragDrop();
                }

                $this.fireDragDropEvent({
                    'dragNodeKey': dragNodeKey,
                    'dropNodeKey': dropNodeKey,
                    'dragSource': dragSource.id,
                    'dndIndex': targetDragNode.prevAll('li.ui-treenode').length,
                    'transfer': transfer
                });
            }
        });
    },
    
    initDropScrollers: function() {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;

        this.jq.prepend('<div class="ui-tree-scroller ui-tree-scrollertop"></div>').append('<div class="ui-tree-scroller ui-tree-scrollerbottom"></div>');
        
        this.jq.children('div.ui-tree-scroller').droppable({
            accept: '.ui-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            over: function() {
                var step = $(this).hasClass('ui-tree-scrollertop') ? -10 : 10;

                $this.scrollInterval = setInterval(function() {
                    $this.scroll(step);
                }, 100);
            },
            out: function() {
                clearInterval($this.scrollInterval);
            }
        });
    },
    
    scroll: function(step) {
        this.container.scrollTop(this.container.scrollTop() + step);
    },
    
    updateDragDropBindings: function(node) {
        //self droppoint
        node.after('<li class="ui-tree-droppoint ui-droppable"></li>');
        this.makeDropPoints(node.next('li.ui-tree-droppoint'));
        
        //descendant droppoints
        var subtreeDropPoints = node.find('li.ui-tree-droppoint');
        subtreeDropPoints.droppable('destroy');
        this.makeDropPoints(subtreeDropPoints);

        //descendant drop node contents
        var subtreeDropNodeContents = node.find('span.ui-treenode-content');
        subtreeDropNodeContents.droppable('destroy');
        this.makeDropNodes(subtreeDropNodeContents);

        if(this.cfg.draggable) {
            subtreeDropNodeContents.data({
                'dragsourceid': this.jqId,
                'dragmode': this.cfg.dragMode
            });
        }
    },
    
    findTargetDragNode: function(dragNode, dragMode) {
        var targetDragNode = null;
        
        if(dragMode === 'self') {
            targetDragNode = dragNode;
        } else if(dragMode === 'parent') {
            targetDragNode = dragNode.parent().closest('li.ui-treenode');
        } else if(dragMode === 'ancestor') {
            targetDragNode = dragNode.parent().parents('li.ui-treenode:last');
        }

        if(targetDragNode.length === 0) {
            targetDragNode = dragNode;
        }
        
        return targetDragNode;
    },
    
    findNodes: function(rowkeys) {
        var nodes = [];
        for(var i = 0; i < rowkeys.length; i++) {
            nodes.push($(this.jqId + '\\:' + rowkeys[i]));
        }
        
        return nodes;
    },
            
    updateRowKeys: function() {
        var children = this.jq.find('> ul.ui-tree-container > li.ui-treenode');
        this.updateChildrenRowKeys(children, null);
    },
            
    updateChildrenRowKeys: function(children, rowkey) {
        var $this = this;
        
        children.each(function(i) {
            var childNode = $(this),
            oldRowKey = childNode.attr('data-rowkey'),
            newRowKey = (rowkey === null) ? i.toString() : rowkey + '_' + i;

            childNode.attr({
                'id': $this.id + ':' + newRowKey,
                'data-rowkey' : newRowKey
            });           

            if(childNode.hasClass('ui-treenode-parent')) {
                $this.updateChildrenRowKeys(childNode.find('> ul.ui-treenode-children > li.ui-treenode'), newRowKey);
            }
        });
    },
            
    validateDropPoint: function(dragNode, dropPoint) {
        //dropped before or after
        if(dragNode.next().get(0) === dropPoint.get(0)||dragNode.prev().get(0) === dropPoint.get(0)) {
            return false;
        }
        
        //descendant of dropnode
        if(dragNode.has(dropPoint.get(0)).length) {
            return false;
        }
        
        //drop restriction
        if(this.cfg.dropRestrict) {
            if(this.cfg.dropRestrict === 'sibling' && dragNode.parent().get(0) !== dropPoint.parent().get(0)) {
                return false;
            }
        }
       
        return true;
    },
            
    validateDropNode: function(dragNode, dropNode, oldParentNode) {
        //dropped on parent
        if(oldParentNode.get(0) === dropNode.get(0))
            return false;
        
        //descendant of dropnode
        if(dragNode.has(dropNode.get(0)).length) {
            return false;
        }
        
        //drop restriction
        if(this.cfg.dropRestrict) {
            if(this.cfg.dropRestrict === 'sibling') {
                return false;
            }
        }
       
        return true;
    },
    
    makeLeaf: function(node) {
        node.removeClass('ui-treenode-parent').addClass('ui-treenode-leaf');
        node.find('> .ui-treenode-content > .ui-tree-toggler').addClass('ui-treenode-leaf-icon').removeClass('ui-tree-toggler ui-icon ui-icon-triangle-1-s');
        node.children('.ui-treenode-children').hide().children().remove();
    },
            
    makeParent: function(node) {
        node.removeClass('ui-treenode-leaf').addClass('ui-treenode-parent');
        node.find('> span.ui-treenode-content > span.ui-treenode-leaf-icon').removeClass('ui-treenode-leaf-icon').addClass('ui-tree-toggler ui-icon ui-icon-triangle-1-e');
        node.children('.ui-treenode-children').append('<li class="ui-tree-droppoint ui-droppable"></li>');
        
        this.makeDropPoints(node.find('> ul.ui-treenode-children > li.ui-tree-droppoint'));
    },
            
    syncDragDrop: function() {
        var $this = this;
        
        if(this.cfg.selectionMode) {
            var selectedNodes = this.findNodes(this.selections);
            
            this.updateRowKeys();
            this.selections = [];
            $.each(selectedNodes, function(i, item) {
                $this.selections.push(item.attr('data-rowkey'));
            });
            this.writeSelections();
        }
        else {
            this.updateRowKeys();
        }
    },
        
    syncDNDCheckboxes: function(dragSource, oldParentNode, newParentNode) {
        if(oldParentNode.length) {
            dragSource.propagateDNDCheckbox(oldParentNode);
        }
        
        if(newParentNode.length) {
            this.propagateDNDCheckbox(newParentNode);
        }
    },
            
    unselectSubtree: function(node) {
        var $this = this;
        
        if(this.isCheckboxSelection()) {
            var checkbox = node.find('> .ui-treenode-content > .ui-chkbox');

            this.toggleCheckboxState(checkbox, true);
        
            node.children('.ui-treenode-children').find('.ui-chkbox').each(function() {
                $this.toggleCheckboxState($(this), true);
            });
        }
        else {
            node.find('.ui-treenode-label.ui-state-highlight').each(function() {
                $(this).removeClass('ui-state-highlight').closest('li.ui-treenode').attr('aria-selected', false);
            });
        }
    },
            
    propagateDNDCheckbox: function(node) {
        var checkbox = node.find('> .ui-treenode-content > .ui-chkbox'),
        children = node.find('> .ui-treenode-children > .ui-treenode');

        if(children.length) {
            if(children.filter('.ui-treenode-unselected').length === children.length)
                this.uncheck(checkbox);
            else if(children.filter('.ui-treenode-selected').length === children.length)
                this.check(checkbox);
            else
                this.partialCheck(checkbox);
        }
        
        var parent = node.parent().closest('.ui-treenode-parent');
        if(parent.length) {
            this.propagateDNDCheckbox(parent);
        }
    },
                        
    fireDragDropEvent: function(event) {
        var $this = this,
        options = {
            source: this.id,
            process: event.transfer ? this.id + ' ' + event.dragSource : this.id
        };

        options.params = [
            {name: this.id + '_dragdrop', value: true},
            {name: this.id + '_dragNode', value: event.dragNodeKey},
            {name: this.id + '_dragSource', value: event.dragSource},
            {name: this.id + '_dropNode', value: event.dropNodeKey},
            {name: this.id + '_dndIndex', value: event.dndIndex}
        ];
        
        if(this.hasBehavior('dragdrop')) {
            var dragdropBehavior = this.cfg.behaviors['dragdrop'];

            dragdropBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    },
            
    isEmpty: function() {
        return (this.container.children().length === 0);
    },
    
    getFirstNode: function() {
        return this.jq.find('> ul.ui-tree-container > li:first-child');
    },
    
    getNodeLabel: function(node) {
        return node.find('> span.ui-treenode-content > span.ui-treenode-label');
    },
    
    focusNode: function(node) {
        if(this.focusedNode) {
            this.getNodeLabel(this.focusedNode).removeClass('ui-treenode-outline');
        }

        this.getNodeLabel(node).addClass('ui-treenode-outline').focus();
        this.focusedNode = node;
    }

});

/**
 * PrimeFaces Horizontal Tree Widget
 */
PrimeFaces.widget.HorizontalTree = PrimeFaces.widget.BaseTree.extend({
        
    init: function(cfg) {
        this._super(cfg);
        
        if(PrimeFaces.isIE()) {
            this.drawConnectors();
        }
    },
    
    //@Override
    bindEvents: function() {
        var $this = this,
        selectionMode = this.cfg.selectionMode,
        togglerSelector = '.ui-tree-toggler',
        nodeContentSelector = '.ui-treenode-content.ui-tree-selectable';

        this.jq.off('click.tree-toggle', togglerSelector)
                    .on('click.tree-toggle', togglerSelector, null, function() {
                        var icon = $(this),
                        node = icon.closest('td.ui-treenode');
                        
                        if(node.hasClass('ui-treenode-collapsed'))
                            $this.expandNode(node);
                        else
                            $this.collapseNode(node);
                    });
                    
        if(selectionMode && this.cfg.highlight) {
            this.jq.off('mouseout.tree mouseover.tree', nodeContentSelector)
                        .on('mouseover.tree', nodeContentSelector, null, function() {
                            var nodeContent = $(this);
                            if(!nodeContent.hasClass('ui-state-highlight')) {
                                nodeContent.addClass('ui-state-hover');
                                
                                if($this.isCheckboxSelection()) {
                                    nodeContent.children('div.ui-chkbox').children('div.ui-chkbox-box').addClass('ui-state-hover');
                                }
                            }
                        })
                        .on('mouseout.tree', nodeContentSelector, null, function() {
                            var nodeContent = $(this);
                            if(!nodeContent.hasClass('ui-state-highlight')) {
                                nodeContent.removeClass('ui-state-hover');
                                
                                if($this.isCheckboxSelection()) {
                                    nodeContent.children('div.ui-chkbox').children('div.ui-chkbox-box').removeClass('ui-state-hover');
                                }
                            }
                        });
        }
        
        this.jq.off('click.tree-content', nodeContentSelector)
                .on('click.tree-content', nodeContentSelector, null, function(e) {
                    $this.nodeClick(e, $(this));
                });

    },
    
    //@Override
    showNodeChildren: function(node) {
        node.attr('aria-expanded', true);
                
        var childrenContainer = node.next(),
        toggleIcon = node.find('> .ui-treenode-content > .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        iconState = this.cfg.iconStates[nodeType];
        
        if(iconState) {
            toggleIcon.nextAll('span.ui-treenode-icon').removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
        }
        
        toggleIcon.addClass('ui-icon-minus').removeClass('ui-icon-plus');
        node.removeClass('ui-treenode-collapsed');
        childrenContainer.show();
        
        if($.browser.msie) {
            this.drawConnectors();
        }
    },
    
    collapseNode: function(node) {
        var childrenContainer = node.next(),
        toggleIcon = node.find('> .ui-treenode-content > .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        iconState = this.cfg.iconStates[nodeType];
        
        if(iconState) {
            toggleIcon.nextAll('span.ui-treenode-icon').addClass(iconState.collapsedIcon).removeClass(iconState.expandedIcon);
        }
        
        toggleIcon.removeClass('ui-icon-minus').addClass('ui-icon-plus');
        node.addClass('ui-treenode-collapsed');
        childrenContainer.hide();
        
        if(this.cfg.dynamic && !this.cfg.cache) {
            childrenContainer.children('.ui-treenode-children').empty();
        }
        
        this.fireCollapseEvent(node);
        
        if($.browser.msie) {
            this.drawConnectors();
        }
    },
    
    //@Override
    getNodeChildrenContainer: function(node) {
        return node.next('.ui-treenode-children-container').children('.ui-treenode-children');
    },
    
    selectNode: function(node, silent) {        
        node.removeClass('ui-treenode-unselected').addClass('ui-treenode-selected').children('.ui-treenode-content').removeClass('ui-state-hover').addClass('ui-state-highlight');

        this.addToSelection(this.getRowKey(node));
        this.writeSelections();

        if(!silent)
            this.fireNodeSelectEvent(node);
    },
    
    unselectNode: function(node, silent) {
        var rowKey = this.getRowKey(node);
           
        node.removeClass('ui-treenode-selected').addClass('ui-treenode-unselected').children('.ui-treenode-content').removeClass('ui-state-highlight');

        this.removeFromSelection(rowKey);
        this.writeSelections();

        if(!silent)
            this.fireNodeUnselectEvent(node);
    },
    
    unselectAllNodes: function() {
        this.selections = [];
        this.jq.find('.ui-treenode-content.ui-state-highlight').each(function() {
            $(this).removeClass('ui-state-highlight').closest('.ui-treenode').attr('aria-selected', false);
        });
    },
    
    preselectCheckbox: function() {
        var _self = this;
        
        this.jq.find('.ui-chkbox-icon').not('.ui-icon-check').each(function() {
            var icon = $(this),
            node = icon.closest('.ui-treenode'),
            childrenContainer = _self.getNodeChildrenContainer(node);

            if(childrenContainer.find('.ui-chkbox-icon.ui-icon-check').length > 0) {
                icon.removeClass('ui-icon-blank').addClass('ui-icon-minus');
            }
        });
    },
    
    toggleCheckboxNode: function(node) {
        var $this = this,
        checkbox = node.find('> .ui-treenode-content > .ui-chkbox'),
        checked = checkbox.find('> .ui-chkbox-box > .ui-chkbox-icon').hasClass('ui-icon-check');

        this.toggleCheckboxState(checkbox, checked);

        if(this.cfg.propagateDown) {
            node.next('.ui-treenode-children-container').find('.ui-chkbox').each(function() {
                $this.toggleCheckboxState($(this), checked);
            });
            
            if(this.cfg.dynamic) {
                this.removeDescendantsFromSelection(node.data('rowkey'));
            }
        }

        if(this.cfg.propagateUp) {
            node.parents('td.ui-treenode-children-container').each(function() {
                var childrenContainer = $(this),
                parentNode = childrenContainer.prev('.ui-treenode-parent'),
                parentsCheckbox = parentNode.find('> .ui-treenode-content > .ui-chkbox'),
                children = childrenContainer.find('> .ui-treenode-children > table > tbody > tr > td.ui-treenode');
                
                if(checked) {
                    if(children.filter('.ui-treenode-unselected').length === children.length)
                        $this.uncheck(parentsCheckbox);
                    else
                        $this.partialCheck(parentsCheckbox);
                }
                else {
                    if(children.filter('.ui-treenode-selected').length === children.length)
                        $this.check(parentsCheckbox);
                    else
                        $this.partialCheck(parentsCheckbox);
                }
            });
        }
        
        this.writeSelections();

        if(checked)
            this.fireNodeUnselectEvent(node);
        else
            this.fireNodeSelectEvent(node);
    },
        
    check: function(checkbox) {
        this._super(checkbox);
        checkbox.parent('.ui-treenode-content').addClass('ui-state-highlight').removeClass('ui-state-hover');
    },
            
    uncheck: function(checkbox) {
        this._super(checkbox);
        checkbox.parent('.ui-treenode-content').removeClass('ui-state-highlight');
    },
            
    drawConnectors: function() {
        this.jq.find('table.ui-treenode-connector-table').each(function() {
            var table = $(this);

            table.height(0).height(table.parent().height());
        });
    },
            
    isEmpty: function() {
        return this.jq.children('table').length === 0;
    },
    
    focusNode: function(node) {
        //focus not supported in horizontal mode
    },
    
    //@Override
    partialCheck: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        treeNode.find('> .ui-treenode-content').removeClass('ui-state-highlight');
        icon.removeClass('ui-icon-blank ui-icon-check').addClass('ui-icon-minus');
        treeNode.removeClass('ui-treenode-selected ui-treenode-unselected').addClass('ui-treenode-hasselected').attr('aria-checked', false).attr('aria-selected', false);
    
        this.removeFromSelection(rowKey);
     }        
    
});
/**
 * PrimeFaces TreeTable Widget
 */
PrimeFaces.widget.TreeTable = PrimeFaces.widget.DeferredWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);        
        this.thead = $(this.jqId + '_head');
        this.tbody = $(this.jqId + '_data');

        this.renderDeferred();
    },
            
    _render: function() {
        if(this.cfg.scrollable) {
            this.setupScrolling();
        }

        if(this.cfg.resizableColumns) {
            this.setupResizableColumns();
        }
        
        this.bindEvents();
    },
    
    refresh: function(cfg) {
        this.columnWidthsFixed = false;
        this.init(cfg);
    },
    
    bindEvents: function() {
        var $this = this,
        togglerSelector = '> tr > td:first-child > .ui-treetable-toggler';
        
        //expand and collapse
        this.tbody.off('click.treeTable-toggle', togglerSelector)
                    .on('click.treeTable-toggle', togglerSelector, null, function(e) {
                        var toggler = $(this),
                        node = toggler.closest('tr');
                        
                        if(!node.data('processing')) {
                            node.data('processing', true);
                            
                            if(toggler.hasClass('ui-icon-triangle-1-e'))
                                $this.expandNode(node);
                            else
                                $this.collapseNode(node);
                        }
                    });
            
        //selection
        if(this.cfg.selectionMode) {
            this.jqSelection = $(this.jqId + '_selection');
            var selectionValue = this.jqSelection.val();
            this.selections = selectionValue === "" ? [] : selectionValue.split(',');

            this.bindSelectionEvents();
        }
        
        //sorting
        this.bindSortEvents();
    },
    
    bindSelectionEvents: function() {
        var $this = this,
        rowSelector = '> tr.ui-treetable-selectable-node';
        
        this.tbody.off('mouseover.treeTable mouseout.treeTable click.treeTable', rowSelector)
                    .on('mouseover.treeTable', rowSelector, null, function(e) {
                        var element = $(this);
                        if(!element.hasClass('ui-state-highlight')) {
                            element.addClass('ui-state-hover');
                        
                            if($this.isCheckboxSelection() && !$this.cfg.nativeElements) {
                                element.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box').addClass('ui-state-hover');
                            }
                        }
                    })
                    .on('mouseout.treeTable', rowSelector, null, function(e) {
                        var element = $(this);
                        if(!element.hasClass('ui-state-highlight')) {
                            element.removeClass('ui-state-hover');
                            
                            if($this.isCheckboxSelection() && !$this.cfg.nativeElements) {
                                element.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box').removeClass('ui-state-hover');
                            }
                        }
                    })
                    .on('click.treeTable', rowSelector, null, function(e) {
                        $this.onRowClick(e, $(this));
                    });
                    
        if(this.isCheckboxSelection()) {
           var checkboxSelector =  this.cfg.nativeElements ? '> tr.ui-treetable-selectable-node > td:first-child :checkbox':
                    '> tr.ui-treetable-selectable-node > td:first-child div.ui-chkbox-box';
                    
                this.tbody.off('click.treeTable-checkbox', checkboxSelector)
                      .on('click.treeTable-checkbox', checkboxSelector, null, function(e) {
                          var node = $(this).closest('tr.ui-treetable-selectable-node');
                          $this.toggleCheckboxNode(node);
                      });
                      
                      
                //initial partial selected visuals
                if(this.cfg.nativeElements) {
                    this.indeterminateNodes(this.tbody.children('tr.ui-treetable-partialselected'));
                }
        }
    },
    
    bindSortEvents: function() {
        var $this = this;
        this.sortableColumns = this.thead.find('> tr > th.ui-sortable-column');
                
        this.sortableColumns.filter('.ui-state-active').each(function() {
            var columnHeader = $(this),
            sortIcon = columnHeader.children('span.ui-sortable-column-icon'),
            sortOrder = null;
            
            if(sortIcon.hasClass('ui-icon-triangle-1-n'))
                sortOrder = 'ASCENDING';
            else
                sortOrder = 'DESCENDING';
            
            columnHeader.data('sortorder', sortOrder);       
        });
        
        this.sortableColumns.on('mouseenter.treeTable', function() {
            var column = $(this);
            
            if(!column.hasClass('ui-state-active'))
                column.addClass('ui-state-hover');
        })
        .on('mouseleave.treeTable', function() {
            var column = $(this);
            
            if(!column.hasClass('ui-state-active'))
                column.removeClass('ui-state-hover');
        })
        .on('click.treeTable', function(e) {
            //Check if event target is not a clickable element in header content
            if($(e.target).is('th,span:not(.ui-c)')) {
                PrimeFaces.clearSelection();

                var columnHeader = $(this),
                sortOrder = columnHeader.data('sortorder')||'DESCENDING';

                if(sortOrder === 'ASCENDING')
                    sortOrder = 'DESCENDING';
                else if(sortOrder === 'DESCENDING')
                    sortOrder = 'ASCENDING';

                $this.sort(columnHeader, sortOrder);
            }
        });
    },
    
    sort: function(columnHeader, order) {  
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            params: [
                {name: this.id + '_sorting', value: true},
                {name: this.id + '_sortKey', value: columnHeader.attr('id')},
                {name: this.id + '_sortDir', value: order}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.tbody.html(content);
                            
                            columnHeader.siblings().filter('.ui-state-active').removeData('sortorder').removeClass('ui-state-active')
                                            .find('.ui-sortable-column-icon').removeClass('ui-icon-triangle-1-n ui-icon-triangle-1-s');
                            
                            columnHeader.removeClass('ui-state-hover').addClass('ui-state-active').data('sortorder', order);
                            var sortIcon = columnHeader.find('.ui-sortable-column-icon');

                            if(order === 'DESCENDING')
                                sortIcon.removeClass('ui-icon-triangle-1-n').addClass('ui-icon-triangle-1-s');
                            else if(order === 'ASCENDING')
                                sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-n');
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args) {
                if($this.cfg.selectionMode && args.selection) {
                    $this.selections = args.selection.split(',');
                    $this.writeSelections();
                }
            }
        };
        
        if(this.hasBehavior('sort')) {
            var sortBehavior = this.cfg.behaviors['sort'];
            sortBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.Request.handle(options); 
        }
    },
    
    expandNode: function(node) {
        var $this = this,
        nodeKey = node.attr('data-rk'),
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_expand', value: nodeKey}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            var nextRow = node.next();
                            node.after(content);
                            node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');
                            node.attr('aria-expanded', true);
                            $this.indeterminateNodes($this.tbody.children('tr.ui-treetable-partialselected'));
                            
                            if(this.cfg.scrollable) {
                                this.alignScrollBody();
                            }
                        }
                    });

                return true;
            },
            oncomplete: function() {
                node.data('processing', false);
            }
        };
        
        if(this.hasBehavior('expand')) {
            var expandBehavior = this.cfg.behaviors['expand'];
            expandBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
    
    collapseNode: function(node) {
        var nodeKey = node.attr('data-rk'),
        nextNodes = node.nextAll();
        
        for(var i = 0; i < nextNodes.length; i++) {
            var nextNode = nextNodes.eq(i),
            nextNodeRowKey = nextNode.attr('data-rk');
            
            if(nextNodeRowKey.indexOf(nodeKey) !== -1) {
               nextNode.remove();
            } 
            else {
                break;
            }
        }
    
        node.attr('aria-expanded', false).find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');
        node.data('processing', false);
        
        if(this.cfg.scrollable) {
            this.alignScrollBody();
        }

        if(this.hasBehavior('collapse')) {
            var collapseBehavior = this.cfg.behaviors['collapse'],
            nodeKey = node.attr('data-rk');

            var ext = {
                params : [
                    {name: this.id + '_collapse', value: nodeKey}
                ]
            };

            collapseBehavior.call(this, ext);
        }
    },
    
    onRowClick: function(event, node) {
        if($(event.target).is('td,span:not(.ui-c)')) {
            var selected = node.hasClass('ui-state-highlight'),
            metaKey = event.metaKey||event.ctrlKey,
            shiftKey = event.shiftKey;
            
            if(this.isCheckboxSelection()) {
                this.toggleCheckboxNode(node);
            }
            else {
                if(selected && metaKey) {
                    this.unselectNode(node);
                }
                else {
                    if(this.isSingleSelection()||(this.isMultipleSelection() && !metaKey)) {
                        this.unselectAllNodes();
                    }

                    if(this.isMultipleSelection() && shiftKey) {
                        this.selectNodesInRange(node);
                    }
                    else {
                        this.selectNode(node);
                        this.cursorNode = node;
                    }
                }
            }
            
            PrimeFaces.clearSelection();
        }
    },
            
    onRowRightClick: function(event, node) {
        var selected = node.hasClass('ui-state-highlight');
        
        if(this.isCheckboxSelection()) {
            if(!selected) {
                this.toggleCheckboxNode(node);
            }
        }
        else {
            if(this.isSingleSelection() || !selected ) {
                this.unselectAllNodes();
            }
            this.selectNode(node);
        }
 
        PrimeFaces.clearSelection();        
    },
    
    selectNode: function(node, silent) {
        var nodeKey = node.attr('data-rk');

        node.removeClass('ui-state-hover ui-treetable-partialselected').addClass('ui-state-highlight').attr('aria-selected', true);
        this.addToSelection(nodeKey);
        this.writeSelections();
        
        if(this.isCheckboxSelection()) {
            if(this.cfg.nativeElements)
                node.find('> td:first-child > :checkbox').prop('checked', true).prop('indeterminate', false);
            else
                node.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box').removeClass('ui-state-hover').children('span.ui-chkbox-icon').removeClass('ui-icon-blank ui-icon-minus').addClass('ui-icon-check');
        }
        
        if(!silent) {
            this.fireSelectNodeEvent(nodeKey);
        }
    },
    
    unselectNode: function(node, silent) {
        var nodeKey = node.attr('data-rk');
        
        node.removeClass('ui-state-highlight ui-treetable-partialselected').attr('aria-selected', false);
        this.removeSelection(nodeKey);
        this.writeSelections();
        
        if(this.isCheckboxSelection()) {
            if(this.cfg.nativeElements)
                node.find('> td:first-child > :checkbox').prop('checked', false).prop('indeterminate', false);
            else
                node.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box > span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check ui-icon-minus');
        }

        if(!silent) {
            this.fireUnselectNodeEvent(nodeKey);
        }
    },
    
    unselectAllNodes: function() {
        var selectedNodes = this.tbody.children('tr.ui-state-highlight'); 
        for(var i = 0; i < selectedNodes.length; i++) {
            this.unselectNode(selectedNodes.eq(i), true);
        }
        
        this.selections = [];
        this.writeSelections();
    },
    
    selectNodesInRange: function(node) {
        if(this.cursorNode) {
            this.unselectAllNodes();

            var currentNodeIndex = node.index(),
            cursorNodeIndex = this.cursorNode.index(),
            startIndex = (currentNodeIndex > cursorNodeIndex) ? cursorNodeIndex : currentNodeIndex,
            endIndex = (currentNodeIndex > cursorNodeIndex) ? (currentNodeIndex + 1) : (cursorNodeIndex + 1),
            nodes = this.tbody.children();

            for(var i = startIndex ; i < endIndex; i++) {
                this.selectNode(nodes.eq(i), true);
            }
        } 
        else {
            this.selectNode(node);
        }
    },
    
    indeterminateNodes: function(nodes) {
        for(var i = 0; i < nodes.length; i++) {
            nodes.eq(i).find('> td:first-child > :checkbox').prop('indeterminate', true);
        }
    },
    
    toggleCheckboxNode: function(node) {;
        var selected = node.hasClass('ui-state-highlight'),
        rowKey = node.data('rk');
     
        //toggle itself
        if(selected)
            this.unselectNode(node, true);
        else
            this.selectNode(node, true);
        
        //propagate down
        var descendants = this.getDescendants(node);
        for(var i = 0; i < descendants.length; i++) {
            var descendant = descendants[i];
            if(selected)
                this.unselectNode(descendant, true);
            else
                this.selectNode(descendant, true);
        }
        
        if(selected) {
           this.removeDescendantsFromSelection(node.data('rk')); 
        }
        
        //propagate up
        var parentNode = this.getParent(node);
        if(parentNode) {
            this.propagateUp(parentNode);
        }
        
        this.writeSelections();
        
        if(selected)
            this.fireUnselectNodeEvent(rowKey);
        else
            this.fireSelectNodeEvent(rowKey);
    },
    
    getDescendants: function(node) {
        var nodeKey = node.attr('data-rk'),
        nextNodes = node.nextAll(),
        descendants = [];
        
        for(var i = 0; i < nextNodes.length; i++) {
            var nextNode = nextNodes.eq(i),
            nextNodeRowKey = nextNode.attr('data-rk');
            
            if(nextNodeRowKey.indexOf(nodeKey) != -1) {
                descendants.push(nextNode);
            } 
            else {
                break;
            }
        }
        
        return descendants;
    },
    
    getChildren: function(node) {
        var nodeKey = node.attr('data-rk'),
        nextNodes = node.nextAll(),
        children = [];
        
        for(var i = 0; i < nextNodes.length; i++) {
            var nextNode = nextNodes.eq(i),
            nextNodeParentKey = nextNode.attr('data-prk');
            
            if(nextNodeParentKey === nodeKey) {
                children.push(nextNode);
            }
        }
        
        return children;
    },
        
    propagateUp: function(node) {
        var children = this.getChildren(node),
        allSelected = true,
        partialSelected = false,
        checkbox = this.cfg.nativeElements ? node.find('> td:first-child > :checkbox') : 
                            node.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box > span.ui-chkbox-icon');

        for(var i = 0; i < children.length; i++) {
            var child = children[i],
            childSelected = child.hasClass('ui-state-highlight');
            
            allSelected = allSelected&&childSelected;
            partialSelected = partialSelected||childSelected||child.hasClass('ui-treetable-partialselected');
        }
        
        if(allSelected) {
            node.removeClass('ui-treetable-partialselected');
            this.selectNode(node, true);
        }
        else if(partialSelected) {
            node.removeClass('ui-state-highlight').addClass('ui-treetable-partialselected');
            
            if(this.cfg.nativeElements)
                checkbox.prop('indeterminate', true);
            else
                checkbox.removeClass('ui-icon-blank ui-icon-check').addClass('ui-icon-minus');
    
            this.removeSelection(node.attr('data-rk'));
        }
        else {
            node.removeClass('ui-state-highlight ui-treetable-partialselected');
            
            if(this.cfg.nativeElements)
                checkbox.prop('indeterminate', false).prop('checked', false);
            else
                checkbox.addClass('ui-icon-blank').removeClass('ui-icon-check ui-icon-minus');
            
            this.removeSelection(node.attr('data-rk'));
        }
        
        var parent = this.getParent(node);
        if(parent) {
            this.propagateUp(parent);
        }
    },
    
    getParent: function(node) {
        var parent = $(this.jqId + '_node_' + node.attr('data-prk'));
        
        return parent.length === 1 ? parent : null;
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    },
            
    removeDescendantsFromSelection: function(rowKey) {
        this.selections = $.grep(this.selections, function(value) {
            return value.indexOf(rowKey + '_') !== 0;
        });
    },
    
    removeSelection: function(nodeKey) {
        this.selections = $.grep(this.selections, function(value) {
            return value !== nodeKey;
        });
    },
    
    addToSelection: function(rowKey) {
        if(!this.isSelected(rowKey)) {
            this.selections.push(rowKey);
        }
    },
    
    isSelected: function(nodeKey) {
        return PrimeFaces.inArray(this.selections, nodeKey);
    },
    
    isSingleSelection: function() {
        return this.cfg.selectionMode == 'single';
    },
    
    isMultipleSelection: function() {
        return this.cfg.selectionMode == 'multiple';
    },
    
    isCheckboxSelection: function() {
        return this.cfg.selectionMode == 'checkbox';
    },
    
    writeSelections: function() {
        this.jqSelection.val(this.selections.join(','));
    },
    
    fireSelectNodeEvent: function(nodeKey) {
        if(this.isCheckboxSelection()) {
            var $this = this,
            options = {
                source: this.id,
                process: this.id
            };
            
            options.params = [
                {name: this.id + '_instantSelection', value: nodeKey}
            ];
            
            options.oncomplete = function(xhr, status, args) {
                if(args.descendantRowKeys && args.descendantRowKeys !== '') {
                    var rowKeys = args.descendantRowKeys.split(',');
                    for(var i = 0; i < rowKeys.length; i++) {
                        $this.addToSelection(rowKeys[i]);
                    }
                    $this.writeSelections();
                }
            }
            
            if(this.hasBehavior('select')) {
                var selectBehavior = this.cfg.behaviors['select'];
                selectBehavior.call(this, options);
            }
            else {
                PrimeFaces.ajax.AjaxRequest(options);
            }
        }
        else {
            if(this.hasBehavior('select')) {
                var selectBehavior = this.cfg.behaviors['select'],
                ext = {
                    params: [
                        {name: this.id + '_instantSelection', value: nodeKey}
                    ]
                };
                
                selectBehavior.call(this, ext);
            }
        }
    },
    
    fireUnselectNodeEvent: function(nodeKey) {
        if(this.hasBehavior('unselect')) {
            var unselectBehavior = this.cfg.behaviors['unselect'],
             ext = {
                params: [
                    {name: this.id + '_instantUnselection', value: nodeKey}
                ]
            };
            
            unselectBehavior.call(this, ext);
        }
    },
    
    setupScrolling: function() {
        this.scrollHeader = this.jq.children('div.ui-treetable-scrollable-header');
        this.scrollBody = this.jq.children('div.ui-treetable-scrollable-body');
        this.scrollFooter = this.jq.children('div.ui-treetable-scrollable-footer');
        this.scrollStateHolder = $(this.jqId + '_scrollState');
        this.scrollHeaderBox = this.scrollHeader.children('div.ui-treetable-scrollable-header-box');
        this.scrollFooterBox = this.scrollFooter.children('div.ui-treetable-scrollable-footer-box');
        this.headerTable = this.scrollHeaderBox.children('table');
        this.bodyTable = this.scrollBody.children('table');
        this.footerTable = this.scrollFooterBox.children('table');
        this.headerCols = this.headerTable.find('> thead > tr > th');
        this.footerCols = this.footerTable.find('> tfoot > tr > td');
        var $this = this;
        
        if(this.cfg.scrollHeight) {
            if(this.cfg.scrollHeight.indexOf('%') !== -1) {
                this.adjustScrollHeight();
            }
        
            var marginRight = this.getScrollbarWidth();
            this.scrollHeaderBox.css('margin-right', marginRight);
            this.scrollFooterBox.css('margin-right', marginRight);
            this.alignScrollBody();
        }
        
        this.fixColumnWidths();
        
        if(this.cfg.scrollWidth) {
            if(this.cfg.scrollWidth.indexOf('%') !== -1) {
                this.adjustScrollWidth();
            }
            else {
                this.setScrollWidth(parseInt(this.cfg.scrollWidth));
            }
        }
        
        this.cloneHead();
        
        this.restoreScrollState();
        
        this.scrollBody.scroll(function() {
            var scrollLeft = $this.scrollBody.scrollLeft();
            $this.scrollHeaderBox.css('margin-left', -scrollLeft);
            $this.scrollFooterBox.css('margin-left', -scrollLeft);
            
            $this.saveScrollState();
        });
        
         this.scrollHeader.on('scroll.treeTable', function() {
            $this.scrollHeader.scrollLeft(0);
        });
        
        this.scrollFooter.on('scroll.treeTable', function() {    
            $this.scrollFooter.scrollLeft(0);
        });
        
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.jq.is(':visible')) {
                if($this.percentageScrollHeight)
                    $this.adjustScrollHeight();
                
                if($this.percentageScrollWidth)
                    $this.adjustScrollWidth();
            }
        });
    },
            
    cloneHead: function() {
        this.theadClone = this.headerTable.children('thead').clone();
        this.theadClone.find('th').each(function() {
            var header = $(this);
            header.attr('id', header.attr('id') + '_clone');
        });
        this.theadClone.removeAttr('id').addClass('ui-treetable-scrollable-theadclone').height(0).prependTo(this.bodyTable);
    },
    
     fixColumnWidths: function() {
        var $this = this;
        
        if(!this.columnWidthsFixed) {
            if(this.cfg.scrollable) {
                this.headerCols.each(function() {
                    var headerCol = $(this),
                    colIndex = headerCol.index(),
                    width = headerCol.width();
                    
                    headerCol.width(width);                    
                    
                    if($this.footerCols.length > 0) {
                        var footerCol = $this.footerCols.eq(colIndex);
                        footerCol.width(width);
                    }
                });
            }
            else {
                this.jq.find('> table > thead > tr > th').each(function() {
                    var col = $(this);
                    col.width(col.width());
                });
            }
            
            this.columnWidthsFixed = true;
        }
    },

    adjustScrollHeight: function() {
        var relativeHeight = this.jq.parent().innerHeight() * (parseInt(this.cfg.scrollHeight) / 100),
        tableHeaderHeight = this.jq.children('.ui-treetable-header').outerHeight(true),
        tableFooterHeight = this.jq.children('.ui-treetable-footer').outerHeight(true),
        scrollersHeight = (this.scrollHeader.outerHeight(true) + this.scrollFooter.outerHeight(true)),
        height = (relativeHeight - (scrollersHeight + tableHeaderHeight + tableFooterHeight));
        
        this.scrollBody.height(height);
    },
            
    adjustScrollWidth: function() {
        var width = parseInt((this.jq.parent().innerWidth() * (parseInt(this.cfg.scrollWidth) / 100)));
        this.setScrollWidth(width);
    },
            
    setOuterWidth: function(element, width) {
        var diff = element.outerWidth() - element.width();
        element.width(width - diff);
    },
            
    hasVerticalOverflow: function() {
        return (this.cfg.scrollHeight && this.bodyTable.outerHeight() > this.scrollBody.outerHeight());
    },
    
    setScrollWidth: function(width) {
        var $this = this;
        this.jq.children('.ui-widget-header').each(function() {
            $this.setOuterWidth($(this), width);
        });
        this.scrollHeader.width(width);
        this.scrollBody.css('padding-right', 0).width(width);
        this.scrollFooter.width(width);
    },
    
    alignScrollBody: function() {
        if(!this.cfg.scrollWidth) {
            if(this.hasVerticalOverflow())
                this.scrollBody.css('padding-right', 0);
            else
                this.scrollBody.css('padding-right', this.getScrollbarWidth());
        }
    },
    
    getScrollbarWidth: function() {        
        return $.browser.webkit ? '15' : PrimeFaces.calculateScrollbarWidth();
    },
            
    restoreScrollState: function() {
        var scrollState = this.scrollStateHolder.val(),
        scrollValues = scrollState.split(',');

        this.scrollBody.scrollLeft(scrollValues[0]);
        this.scrollBody.scrollTop(scrollValues[1]);
    },
    
    saveScrollState: function() {
        var scrollState = this.scrollBody.scrollLeft() + ',' + this.scrollBody.scrollTop();
        
        this.scrollStateHolder.val(scrollState);
    },
    
    setupResizableColumns: function() {
        this.fixColumnWidths();
        
        if(!this.cfg.liveResize) {
            this.resizerHelper = $('<div class="ui-column-resizer-helper ui-state-highlight"></div>').appendTo(this.jq);
        }
        
        this.thead.find('> tr > th.ui-resizable-column:not(:last-child)').prepend('<span class="ui-column-resizer">&nbsp;</span>');        
        var resizers = this.thead.find('> tr > th > span.ui-column-resizer'),
        $this = this;
            
        resizers.draggable({
            axis: 'x',
            start: function() {
                if($this.cfg.liveResize) {
                    $this.jq.css('cursor', 'col-resize');
                }
                else {
                    var height = $this.cfg.scrollable ? $this.scrollBody.height() : $this.thead.parent().height() - $this.thead.height() - 1;
                    $this.resizerHelper.height(height);
                    $this.resizerHelper.show();
                }
            },
            drag: function(event, ui) {
                if($this.cfg.liveResize) {
                    $this.resize(event, ui);
                }
                else {
                    $this.resizerHelper.offset({
                        left: ui.helper.offset().left + ui.helper.width() / 2, 
                        top: $this.thead.offset().top + $this.thead.height()
                    });  
                }                
            },
            stop: function(event, ui) {
                var columnHeader = ui.helper.parent();
                ui.helper.css('left','');
                
                if($this.cfg.liveResize) {
                    $this.jq.css('cursor', 'default');
                } else {
                    $this.resize(event, ui);
                    $this.resizerHelper.hide();
                }
                
                var options = {
                    source: $this.id,
                    process: $this.id,
                    params: [
                        {name: $this.id + '_colResize', value: true},
                        {name: $this.id + '_columnId', value: columnHeader.attr('id')},
                        {name: $this.id + '_width', value: columnHeader.width()},
                        {name: $this.id + '_height', value: columnHeader.height()}
                    ]
                }
                
                if($this.hasBehavior('colResize')) {
                    $this.cfg.behaviors['colResize'].call($this, options);
                }
            },
            containment: this.jq
        });
    },
    
    resize: function(event, ui) {
        var columnHeader = ui.helper.parent(),
        nextColumnHeader = columnHeader.next(),
        change = null, newWidth = null, nextColumnWidth = null;
        
        if(this.cfg.liveResize) {
            change = columnHeader.outerWidth() - (event.pageX - columnHeader.offset().left),
            newWidth = (columnHeader.width() - change),
            nextColumnWidth = (nextColumnHeader.width() + change);
        } 
        else {
            change = (ui.position.left - ui.originalPosition.left),
            newWidth = (columnHeader.width() + change),
            nextColumnWidth = (nextColumnHeader.width() - change);
        }
        
        if(newWidth > 15 && nextColumnWidth > 15) {
            columnHeader.width(newWidth);
            nextColumnHeader.width(nextColumnWidth);
            var colIndex = columnHeader.index();

            if(this.cfg.scrollable) {
                this.theadClone.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth);
                this.theadClone.find(PrimeFaces.escapeClientId(nextColumnHeader.attr('id') + '_clone')).width(nextColumnWidth);

                if(this.footerCols.length > 0) {
                    var footerCol = this.footerCols.eq(colIndex),
                    nextFooterCol = footerCol.next();

                    footerCol.width(newWidth);
                    nextFooterCol.width(nextColumnWidth);
                }
            }
        }
    }
});
/**
 * PrimeFaces Wizard Component
 */
PrimeFaces.widget.Wizard = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.content = $(this.jqId + '_content');
        this.backNav = $(this.jqId + '_back');
        this.nextNav = $(this.jqId + '_next');
        this.cfg.formId = this.jq.parents('form:first').attr('id');
        this.currentStep = this.cfg.initialStep;
        
        var _self = this;
        
        //Step controls
        if(this.cfg.showStepStatus) {
            this.stepControls = $(this.jqId + ' .ui-wizard-step-titles li.ui-wizard-step-title');
        }

        //Navigation controls
        if(this.cfg.showNavBar) {
            var currentStepIndex = this.getStepIndex(this.currentStep);
            
            //visuals
            PrimeFaces.skinButton(this.backNav);
            PrimeFaces.skinButton(this.nextNav);

            //events
            this.backNav.click(function() {_self.back();});
            this.nextNav.click(function() {_self.next();});

            if(currentStepIndex == 0)
                this.backNav.hide();
            else if(currentStepIndex == this.cfg.steps.length - 1)
                this.nextNav.hide();
        }
    },
    
    back: function() {
        if(this.cfg.onback) {
            var value = this.cfg.onback.call(this);
            if(value === false) {
                return;
            }
        }

        var targetStepIndex = this.getStepIndex(this.currentStep) - 1;
        if(targetStepIndex >= 0) {
            var stepToGo = this.cfg.steps[targetStepIndex];
            this.loadStep(stepToGo, true);
        }
    },
    
    next: function() {
        if(this.cfg.onnext) {
            var value = this.cfg.onnext.call(this);
            if(value === false) {
                return;
            }
        }

        var targetStepIndex = this.getStepIndex(this.currentStep) + 1;
        if(targetStepIndex < this.cfg.steps.length) {
            var stepToGo = this.cfg.steps[targetStepIndex];
            this.loadStep(stepToGo, false);
        }
    },
    
    loadStep: function(stepToGo, isBack) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId,
            params: [
                {name: this.id + '_wizardRequest', value: true},
                {name: this.id + '_stepToGo', value: stepToGo}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args) {
                $this.currentStep = args.currentStep;
                
                if(!args.validationFailed) {
                    var currentStepIndex = $this.getStepIndex($this.currentStep);

                    if($this.cfg.showNavBar) {
                        if(currentStepIndex === $this.cfg.steps.length - 1) {
                            $this.hideNextNav();
                            $this.showBackNav();
                        } else if(currentStepIndex === 0) {
                            $this.hideBackNav();
                            $this.showNextNav();
                        } else {
                            $this.showBackNav();
                            $this.showNextNav();
                        }
                    }

                    //update step status
                    if($this.cfg.showStepStatus) {
                        $this.stepControls.removeClass('ui-state-highlight');
                        $($this.stepControls.get(currentStepIndex)).addClass('ui-state-highlight');
                    }
                }
            }
        };

        if(isBack) {
            options.params.push({name: this.id + '_backRequest', value: true});
        }

        PrimeFaces.ajax.Request.handle(options);
    },
    
    getStepIndex: function(step) {
        for(var i=0; i < this.cfg.steps.length; i++) {
            if(this.cfg.steps[i] == step)
                return i;
        }

        return -1;
    },
    
    showNextNav: function() {
        this.nextNav.fadeIn();
    },
    
    hideNextNav: function() {
        this.nextNav.fadeOut();
    },
    
    showBackNav: function() {
        this.backNav.fadeIn();
    },
    
    hideBackNav: function() {
        this.backNav.fadeOut();
    }
    
});

