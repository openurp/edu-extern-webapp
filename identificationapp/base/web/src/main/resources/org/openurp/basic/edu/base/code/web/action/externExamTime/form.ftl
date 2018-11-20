[#ftl]
[@b.head/]
  [@b.toolbar title="考试项目基础数据管理配置"]
    bar.addBack();
  [/@]
  [@b.form name="examTimeForm" action="!save" target="examTimes" theme="list"]
    [@b.textfield label="代码" name="examTime.code" value=(examTime.code)! required="true" maxlength="20" style="width:200px"/]
    [@b.validity]
      $("[name='examTime.code']", document.examTimeForm).require().assert(function() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": document.examTimeForm["examTime.id"].value,
            "code": document.examTimeForm["examTime.code"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "代码已存在！！！");
    [/@]

    [@b.textfield label="名称" name="examTime.name" value=(examTime.name)! required="true" maxlength="100" style="width:200px"/]
    [@b.datepicker id="beginOn" label="启用日期" name="examTime.beginOn" value=(examTime.beginOn?string('yyyy-MM-dd'))?default('') required="true" style="width:200px" format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'endOn\\')}"/]
    [@b.datepicker id="endOn" label="截止日期" name="examTime.endOn" value=(examTime.endOn?string('yyyy-MM-dd'))?default('') style="width:200px" format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'beginOn\\')}"/]
    [@b.formfoot]
      <input type="hidden" name="examTime.id" value="${(examTime.id)!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
[@b.foot/]
