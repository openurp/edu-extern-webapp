[#ftl]
[@b.head/]
  [@b.toolbar title="证书级别基础数据管理配置"]
    bar.addBack();
  [/@]
  [@b.form name="levelForm" action="!save" target="levels" theme="list"]
    [@b.textfield label="代码" name="level.code" value=(level.code)! required="true" maxlength="20" style="width:200px"/]
    [@b.validity]
      $("[name='level.code']", document.levelForm).require().assert(function() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": document.levelForm["level.id"].value,
            "code": document.levelForm["level.code"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "代码已存在！！！");
    [/@]

    [@b.textfield label="名称" name="level.name" value=(level.name)! required="true" maxlength="100" style="width:200px"/]
    [@b.datepicker id="beginOn" label="启用日期" name="level.beginOn" value=(level.beginOn?string('yyyy-MM-dd'))?default('') required="true" style="width:200px" format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'endOn\\')}"/]
    [@b.datepicker id="endOn" label="截止日期" name="level.endOn" value=(level.endOn?string('yyyy-MM-dd'))?default('') style="width:200px" format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'beginOn\\')}"/]
    [@b.formfoot]
      <input type="hidden" name="level.id" value="${(level.id)!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
[@b.foot/]
