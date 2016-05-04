/**
 * Cxj CodeMirror Widget
 */
/* global PrimeFaces, cxj*/
PrimeFaces.widget.CodeMirror = PrimeFaces.widget.DeferredWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        if (this.jq.next().hasClass('CodeMirror')) {
            this.jq.next().remove();
        }
        this.options = this.cfg;
        var scriptUris = [];
        for (var opKey in this.cfg) {
            if (/^script_/.test(opKey)) {
                scriptUris.push(PrimeFaces.getFacesResource(this.cfg[opKey].name, this.cfg[opKey].library));
            }
            if (/^css_/.test(opKey)) {
                this.addCssResource(this.cfg[opKey].library, this.cfg[opKey].name);
            }
        }
        this.options.onChange =
                $.proxy(function (from, to, text, next) {
                    //set value to textarea
                    this.cm.save();
                }, this);
        var $this = this;
        if (this.jq.is(':visible')) {
            this.flickerHank = true;
            this.jq.hide();
        }
        this.addScriptResources(scriptUris, function () {
            $this.renderDeferred();
        });
    },
    renderDeferred: function () {
        if (this.flickerHank) {
            this.flickerHank = false;
            this.jq.show();
            this._render();
            this.postRender();
        } else {
            this._super();
        }
    },
    _render: function () {
        this.cm = CodeMirror.fromTextArea(document.getElementById(this.id), this.options);
        this.cm.widget = this;
    },
    needJsonParse: function (toTest) {
        return (typeof toTest === 'string' && /^\{|JSON|\}/.test(toTest));
    },
    unpackJsonAttribute: function (packed) {
        return packed.substring(8);
    },
    isJsonObjectString: function (toTest) {
        var trimed = $.trim(toTest);
        return (/^{/.test(trimed) && /}$/.test(trimed)) || trimed === "null";
    },
    addCssResource: function (library, name) {
        var uri = PrimeFaces.getFacesResource(name, library);
        var cssResource = '<link type="text/css" rel="stylesheet" href="' + uri + '" />';
        if ($('link[href*="' + uri + '"]').length === 0) {
            $('head').append(cssResource);
        }
    },
    addScriptResources: function (uris, callback) {
        this._addScriptResource(uris, 0, callback);
    },
    _addScriptResource: function (uris, idx, callback) {
        if (idx < uris.length) {
            var $this = this;
            if (!cxj.resource.exits(uris[idx])) {
                cxj.resource.push(uris[idx]);
                $.ajax({
                    type: "GET",
                    url: uris[idx],
                    dataType: "script",
                    error: function () {
                        console.log("error");
                    },
                    success: function () {
                        $this._addScriptResource(uris, idx + 1, callback);
                    },
                    cache: true
                });
            } else {
                $this._addScriptResource(uris, idx + 1, callback);
            }
        }
        if (idx === uris.length) {
            callback();
        }
    },
    parseOptions: function () {
        var options = {};
        var args = arguments;
        var $this = this;
        $.each(this.cfg, function (key, value) {
            if (args.length > 0) {
                if ($.inArray(key, args) !== -1) {
                    if ($this.needJsonParse(value)) {
                        options[key] = JSON.parse($this.unpackJsonAttribute(value));
                    } else {
                        options[key] = value;
                    }
                }
            } else {
                if ($this.needJsonParse(value)) {
                    options[key] = JSON.parse($this.unpackJsonAttribute(value));
                } else {
                    options[key] = value;
                }
            }
        });
        return options;
    },
    hasBehavior: function (event) {
        if (this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }

        return false;
    }
});
