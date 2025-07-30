/**
 * Cxj TableMenu Widget
 */
/* global PrimeFaces */
PrimeFaces.widget.TableMenu = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        this.inserter = this.jq.children("span.ui-table-inserter");
        this.hasInserter = this.inserter[0] !== undefined;
        this.deleter = this.jq.children("span.ui-table-deleter");
        this.hasDeleter = this.deleter[0] !== undefined;
        this.editing = false;
        this.bindEvents();
    },
    getTableWidget: function () {
        return PrimeFaces.getWidgetById(this.jq.closest(".ui-datatable").attr("id"));
    },
    getTemplateRow: function () {
        var template = this.jq.children("div.ui-template-row").html();
        return template.substring(4, template.length - 3);
    },
    hasBehavior: function (event) {
        if (this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }

        return false;
    },
    bindEvents: function () {
        this.bindInserterEvents();
        this.bindDeleterEvents();
    },
    createInserterRow: function () {
        var table = this.getTableWidget();
        table.getTbody().children("tr:last").after(this.getTemplateRow());
        var newRow = table.getTbody().children("tr:last");
        var editor = table.getTbody().children("tr:last").find("div.ui-row-editor");
        newRow.children('td.ui-editable-column').each(function () {
            var column = $(this);
            column.find('.ui-cell-editor-output').hide();
            column.find('.ui-cell-editor-input').show();
        });
        editor.removeClass("ui-row-editor").addClass("ui-row-inserter ui-helper-clearfix");
        editor.children(".ui-icon-pencil").hide();
        var that = this;
        editor.children(".ui-icon-check").show().click(function () {
            that.doInsertRequest(newRow, "save");
            that.editing = false;
        });
        editor.children(".ui-icon-close").show().click(function () {
            that.doInsertRequest(newRow, "cancel");
            that.editing = false;
        });
        return newRow;
    },
    getInserterRow: function () {
        return this.getTableWidget().jq.find(".ui-row-inserter").closest("tr");
    },
    invalidateInserterRow: function () {
        this.getInserterRow().addClass("ui-state-error");
    },
    bindInserterEvents: function () {
        if (this.hasInserter) {
            var that = this;
            this.inserter.click(function () {
                if (!that.editing) {
                    var newRow = that.createInserterRow();
                    that.doInsertRequest(newRow, "init");
                    that.editing = true;
                }
            });
        }
    },
    getInsertUpdate: function (action) {
        if (action === "init") {
            return this.id;
        } else if (action === "save") {
            return this.id + " " + this.getTableWidget().id;
        } else {
            return this.id + " " + this.getTableWidget().id;
        }
    },
    doInsertRequest: function (row, action) {
        var $this = this, options = {
            source: this.id,
            process: this.id,
            update: this.getInsertUpdate(action),
            formId: this.cfg.formId,
            params: [
                {name: this.id + '_rowInsertAction', value: action}
            ],
//            onsuccess: function (responseXML, status, xhr) {
//                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
//                    widget: $this.getTableWidget(),
//                    handle: function (content) {
//                        this.jq.replaceWith(content);
//                    }
//                });
//                return true;
//            },
            oncomplete: function (xhr, status, args) {
                if (args && args.validationFailed && action === "save") {
                    $this.createInserterRow();
                    $this.invalidateInserterRow();
                }
            }
        };

        if (action === 'save') {
            this.getTableWidget().getRowEditors(row).each(function () {
                options.params.push({name: this.id, value: this.id});
            });
        }

        if (action === 'init' && this.hasBehavior('insertInit')) {
            this.cfg.behaviors['insertInit'].call(this, options);
        } else if (action === 'save' && this.hasBehavior('insertInvoke')) {
            this.cfg.behaviors['insertInvoke'].call(this, options);
        } else if (action === 'cancel' && this.hasBehavior('insertCancel')) {
            this.cfg.behaviors['insertCancel'].call(this, options);
        } else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
    bindDeleterEvents: function () {
        if (this.hasDeleter) {
            var $this = this;
            this.deleter.click(function () {
                $this.doDeleteRequest();
            });
        }
    },
    doDeleteRequest: function () {
        var options = {
            source: this.id,
            process: this.id,
            update: this.getTableWidget().id,
            formId: this.cfg.formId,
            params: [
                {name: this.id + '_rowDeleteAction', value: this.getTableWidget().selection.join(',')}
            ],
            oncomplete: function (xhr, status, args) {
                if (args && args.validationFailed) {

                }
            }
        };

        if (this.hasBehavior('delete')) {
            this.cfg.behaviors['delete'].call(this, options);
        } else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

});