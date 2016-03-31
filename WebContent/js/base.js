/**
 * Created by dongwenqi on 16-3-1.
 */
var genericJsonRpc = function(params, fct) {
    var data = {
        jsonrpc: "2.0",
        params: params,
        id: Math.floor(Math.random() * 1000 * 1000 * 1000)
    };
    var xhr = fct(data);
    var result = xhr.pipe(function(result) {
        if (result.error !== undefined) {
            return $.Deferred().reject("server", result.error);
        } else {
            return result;
        }
    }, function() {
        var def = $.Deferred();
        return def.reject.apply(def, ["communication"].concat(_.toArray(arguments)));
    });
    result.abort = function () { xhr.abort && xhr.abort(); };
    return result;
};

var easyjava = new Object({
    init: function () {
        var start = this.start();
        this.url = this.getUrl();
        return start;
    },
    getUrl: function() {
    	var self  = this;
    	var  url = window.location.href;
    	if (url.indexOf("#", 0)>0){
    		var parameters=url.substring(url.indexOf("#", 1)+1,url.length).split("&");
    		url=  url.substring(0, url.indexOf("#", 1));
    		res = {};
    		_.each(parameters,function(para){
    			res[para.substring(0,para.indexOf("=", 1))] = para.substring(para.indexOf("=", 1)+1,para.length);
    		});
    		self.loadview(res);
    	}
    	if (url.indexOf("?", 0)>0){
    		url=  url.substring(0, url.indexOf("?", 1));
    	}    	
    	return url;
	},
    start: function () {
        var self = this;

        $('.e_panel_submit').live('click', function () {
            event.preventDefault();
            var cr = $(this);
            //this.url = cr.context.baseURI;
            var id  = null;
            while(!id){
                cr = cr.parent();
                id = cr.find('.e_form').attr("data-id");
            }
            return self.add(self.read_field(cr,id)).done(function(data){
            	if(/^[0-9]+$/.test(data)){
            		self.read_rpc("forum", data, "view");
            	}
            });
        });
    },
    read_field : function(cr,id){
        var res = {id:id}
        _.each(cr.find('input'),function(node){
            res[node.getAttribute('name')] = $(node).val();
        });
        _.each(cr.find('textarea'),function(node){
            res[node.getAttribute('name')] = $(node).val();
        });
        return res;
    },
    add: function(field_list){
        return this.add_rpc(field_list);
    },
    
    loadview:function(res){
    	 var self = this;
         return genericJsonRpc(res, function (data) {
             return $.ajax(self.url+'_rpc_loadview', _.extend({}, '', {
                 url: self.url+'_rpc_loadview',
                 dataType: 'json',
                 type: 'POST',
                 data: JSON.stringify(data, ''),
                 contentType: 'application/json'
             }));
         });
    },

    add_rpc: function (field_list) {
        var self = this;
        return genericJsonRpc(field_list, function (data) {
            return $.ajax(self.url+'_rpc_add', _.extend({}, '', {
                url: self.url+'_rpc_add',
                dataType: 'json',
                type: 'POST',
                data: JSON.stringify(data, ''),
                contentType: 'application/json'
            }));
        });
    },
    
    read_rpc: function (model,id,operator) {
        var self = this;
		if(operator=='view'){
			window.location.hash ="model="+model+"&id="+id+"&view="+operator;
			$.ajax({
				type:'get',
				url:self.url+'_rpc_read',
				data:{model:model,id:id},
				async:true,
				success:function(msg){
					$('.e_form').html(msg);
					$('.e_button_edit').html("");
				},
	            error: function () {
	                self.throw_err("<h3>警告</h3><p>网络请求 <strong>失败</strong>检查您的网络连接和设备环境</p><br/>");
	            }
			});
		}
	},

    throw_err : function(msg){
		alertify.alert(msg);
    },


});
easyjava.init();
