var A_ComboBox=document.registerElement(
	'a-combobox',{
		prototype:Object.create(
			HTMLDivElement.prototype,{
				createdCallback:{
					value:function(){

						var a_id=this.id;
						this.tcb=document.createElement('input');
						this.tcb.type='checkbox';
						this.tcb.id="_tcp_"+a_id;
						this.tlbl=document.createElement('label');
						this.tlbl.id="_tlbl_"+a_id;
						this.tlbl.innerHTML="tlbl";
						this.tlbl.htmlFor=this.tcb.id;

						this.hcb=document.createElement('input');
						this.hcb.type='checkbox';
						this.hcb.id="_tcp_"+a_id;
						this.hlbl=document.createElement('label');
						this.hlbl.id="_tlbl_"+a_id;
						this.hlbl.innerHTML="hlbl";
						this.hlbl.htmlFor=this.hcb.id;

						this.combo=document.createElement("select");
						this.combo.id="_combo_"+a_id;

						this.hdiv=document.createElement('div');
						this.hdiv.id="_hdiv_"+this.id;
						this.hdiv.appendChild(this.combo);
						this.hdiv.appendChild(this.hcb);
						this.hdiv.appendChild(this.hlbl);


						this.appendChild(this.tcb);
						this.appendChild(this.tlbl);
						this.appendChild(this.hdiv);
					}
				}

			}
		)
	}
);

A_ComboBox.prototype.setdata=function(value){
	combo=this.querySelector("#_combo_"+this.id);
	var maxval="";
	value.forEach(function(value,key){
		op=document.createElement("option");
		op.value=key;
		op.appendChild(document.createTextNode(value));
		combo.appendChild(op);
		if(maxval.length<value.length){
			maxval=value;
		}
	});
	A_ComboBox.prototype.setdatabinding(combo.id);
	var tmp=document.createElement("span");
	tmp.className="input-elementtmp-element";
	tmp.innerHTML=maxval.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
	tmp.style.fontFamily="Arial,Helvetica,sans-serif";
	document.body.appendChild(tmp);
	var theWidth=0;
	theWidth=tmp.getBoundingClientRect().width;
	document.body.removeChild(tmp);
	var style=document.createElement('style');
	style.innerHTML=".e-sel-"+combo.id+"{width:"+((theWidth+24).toFixed(2))+"px;height:20px;}"
	console.log(style);
	document.head.appendChild(style);
}

A_ComboBox.prototype.getcombodata=function(value){
	combo=this.querySelector("#_combo_"+this.id);
	console.log(combo.options[combo.selectedIndex].text);
}

A_ComboBox.prototype.setdatabinding=function(acom_id){
	$(function(){
		$.widget("custom.combobox",{
			_create:function(){
				this.wrapper=$("<span>")
				.addClass("custom-combobox")
				.insertAfter(this.element);
				this.element.hide();
				this._createAutocomplete();
				this._createShowAllButton();
			},

			_createAutocomplete:function(){
				var selected=this.element.children(":selected"),
				value=selected.val()?selected.text():"";

				this.input=$("<input>")
					.attr("id"	,"inp"+acom_id)
					.appendTo(this.wrapper)
					.val(value)
					.attr("title","")
					.addClass("e-sel-"+acom_id)
					.autocomplete({
						delay:0,
						minLength:0,
						source:$.proxy(this,"_source"),
						autoFocus:true
					})
					.tooltip({
						classes:{"ui-tooltip":"ui-state-highlight"}
					})
					.removeClass("ui-autocomplete-input");

				this._on(
					this.input,{
						autocompleteselect:function(event,ui){
							ui.item.option.selected=true;
							this._trigger("select",event,{
								item:ui.item.option
							});
						},

						autocompletechange:"_removeIfInvalid"
					}
				);
			},

			_createShowAllButton:function(){
				var input=this.input,
					wasOpen=false;

					$("<a>")
						.attr("tabIndex",-1)
						.attr("title","ShowAllItems")
						.tooltip()
						.appendTo(this.wrapper)
						.button({
							icons:{
								primary:"ui-icon-triangle-1-s"
							},
							text:false
						})
						.removeClass("ui-corner-all")
						.addClass("custom-combobox-toggleui-corner-right")
						.on("mousedown",function(){
							wasOpen=input.autocomplete("widget").is(":visible");
						})
						.on("click",function(){
							input.trigger("focus");

							//Closeifalreadyvisible
							if(wasOpen){
								return;
							}

							//Passemptystringasvaluetosearchfor,displayingallresults
							input.autocomplete("search","");
						});
			},

			_source:function(request,response){
				var matcher=new RegExp($.ui.autocomplete.escapeRegex(request.term),"i");
				response(this.element.children("option").map(function(){
					var text=$(this).text();
					//if(this.value&&(!request.term||matcher.test(text)))
					return{
						label:text,
						value:text,
						option:this
					};
				}));
			},

			_removeIfInvalid:function(event,ui){

				//Selectedanitem,nothingtodo
				if(ui.item){
					return;
				}

				//Searchforamatch(case-insensitive)
				var value=this.input.val(),
				valueLowerCase=value.toLowerCase(),
				valid=false;
				this.element.children("option").each(function(){
					if($(this).text().toLowerCase()===valueLowerCase){
						this.selected=valid=true;
						returnfalse;
					}
				});

				//Foundamatch,nothingtodo
				if(valid){
					return;
				}

				//Removeinvalidvalue
				this.input
				.val("")
				.attr("title",value+"didn'tmatchanyitem")
				.tooltip("open");
				this.element.val("");
				this._delay(
					function(){
						this.input.tooltip("close").attr("title","");
					},
				2500);
				this.input.autocomplete("instance").term="";
			},

			_destroy:function(){
				this.wrapper.remove();
				this.element.show();
			}
		});

		$("#"+acom_id).combobox();
		$("#toggle").on("click",function(){
			$("#combobox").toggle();
		});
	});
}
