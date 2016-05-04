PrimeFaces.widget.LayoutEx = PrimeFaces.widget.DeferredWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        if (cfg.fullPage) {
            this.jq = $("body");
        }
        this.options = cfg.userOptions;
        this.options = $.layout.transformData(this.options, true);
        this.options = $.extend(true, this.options, cfg.builtinOptions);
        if (!this.options.name) {
            this.options.name = ("layout_" + this.id).replace(/:/g, "_");
        }
        this.options.stateManagement = {
            enabled: true
        };
        this.renderDeferred();
    },
    _render: function () {
        this.layout = this.jq.layout(this.options);
        //this._fixPaneClass(this.jq);
        //this.fixPaneClass(this.layout, true);
        this.bindEvents();
    },
    fixPaneClass: function (_layout, _recursion) {
        var panes = _layout.panes;
        if (panes.east) {
            this._fixPaneClass(panes.east);
        }
        if (panes.south) {
            this._fixPaneClass(panes.south);
        }
        if (panes.west) {
            this._fixPaneClass(panes.west);
        }
        if (panes.north) {
            this._fixPaneClass(panes.north);
        }
        if (panes.center) {
            this._fixPaneClass(panes.center);
        }
        if (_recursion) {
            if (_layout.children) {
                var $this = this;
                $.each(_layout.children, function (k, v) {
                    if (v) {
                        $.each(v, function (_k, _v) {
                            if ($this._isLayout(_v)) {
                                $this.fixPaneClass(v, _recursion);
                            }
                        });
                    }
                });
            }
        }
    },
    _fixPaneClass: function (pane) {
        var classNames = pane.attr('class').split(' ');
        var toFixes = [];
        $.each(classNames, function (i, value) {
            if (value && !(/^ui-layout-/.test(value))) {
                toFixes.push(value);
            }
        });
        $.each(toFixes, function (i, value) {
            pane.removeClass(value).addClass(value);
        });
    },
    _isLayout: function (_layout) {
        return _layout && _layout["panes"] !== undefined;
    },
    bindEvents: function () {
    },
    hasBehavior: function (event) {
        if (this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }

        return false;
    }
});