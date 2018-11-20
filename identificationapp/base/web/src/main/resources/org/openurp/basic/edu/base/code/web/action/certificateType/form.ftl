[#ftl]
[@b.head/]
  [@b.toolbar title="证书类型（考试科目/证书子类）基础数据管理配置"]
    bar.addBack();
  [/@]
  [@b.form name="typeForm" action="!save" target="types" theme="list"]
    [@b.textfield label="代码" name="type.code" value=(type.code)! required="true" maxlength="20" style="width:200px"/]
    [@b.validity]
      $("[name='type.code']", document.typeForm).require().assert(function() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": document.typeForm["type.id"].value,
            "code": document.typeForm["type.code"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "代码已存在！！！");
    [/@]

    [@b.textfield label="名称" name="type.name" value=(type.name)! required="true" maxlength="100" style="width:200px"/]
    [@b.select label="考试项目" name="type.examSubject.id" items=examSubjects?sort_by(["name"]) value=(type.examSubject.id)! required="true" style="width:200px"/]
    [@b.datepicker id="beginOn" label="启用日期" name="type.beginOn" value=(type.beginOn?string('yyyy-MM-dd'))?default('') required="true" style="width:200px" format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'endOn\\')}"/]
    [@b.datepicker id="endOn" label="截止日期" name="type.endOn" value=(type.endOn?string('yyyy-MM-dd'))?default('') style="width:200px" format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'beginOn\\')}"/]
    [@b.formfoot]
      <input type="hidden" name="type.id" value="${(type.id)!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
[@b.foot/]
