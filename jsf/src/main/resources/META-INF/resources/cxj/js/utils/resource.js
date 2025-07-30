(function (window) {
    if (!window.cxj) {
        window.cxj = {};
    }
    if (!window.cxj.resource) {
        window.cxj.resource = {
            flags: [],
            exits: function (uri) {
                return $.inArray(uri, this.flags) >= 0;
            },
            push: function (uri) {
                this.flags.push(uri);
            }
        };
    }
})(window);

