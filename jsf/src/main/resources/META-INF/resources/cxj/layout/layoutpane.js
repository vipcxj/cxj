PrimeFaces.widget.LayoutPaneEx = PrimeFaces.widget.DeferredWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        this.jq = $(PrimeFaces.escapeClientId(this.id + "_content"));
        if (cfg.hasSubPane) {
            this.options = cfg.userOptions;
            this.options = $.layout.transformData(this.options, true);
            this.options = $.extend(true, this.options, cfg.builtinOptions);
            if (!this.options.name) {
                this.options.name = ("layout_" + this.id).replace(/:/g, "_");
            }
        }
        this.renderDeferred();
    },
    _render: function () {
        if (this.cfg.hasSubPane) {
            var parentWidget = PrimeFaces.widgets[this.cfg.parentWidget];
            if (parentWidget && parentWidget.layout) {
                parentWidget.layout[this.cfg.position].options.children = this.options;
            }
        }
        if (this.isRefresh) {
            var parentWidget = PrimeFaces.widgets[this.cfg.parentWidget];
            if (parentWidget && parentWidget.layout) {
                var left = parentWidget.layout.destroy();
                parentWidget.layout = left.container.layout(left.options);
                //this.fixPaneClass(parentWidget.layout, true);
                if (this.cfg.hasSubPane) {
                    this.layout = parentWidget.layout[this.cfg.position].children[this.options.name];
                }
            }
            this.isRefresh = false;
        } else {
            if (this.cfg.hasSubPane) {
                this.layout = this.jq.layout(this.options);
                //this.fixPaneClass(this.layout, true);
            }
        }
        this.bindEvents();
    },
    refresh: function (cfg) {
        this.isRefresh = true;
        this._super(cfg);
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


