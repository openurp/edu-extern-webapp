[#ftl]
[@b.head/]
  [@b.toolbar title="证书大类（考试项目）基础数据管理配置"]
    bar.addBack();
  [/@]
  [@b.form name="examSubjectForm" action="!save" target="examSubjects" theme="list"]
    [@b.textfield label="代码" name="examSubject.code" value=(examSubject.code)! required="true" maxlength="20" style="width:200px"/]
    [@b.validity]
      $("[name='examSubject.code']", document.examSubjectForm).require().assert(function() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": document.examSubjectForm["examSubject.id"].value,
            "code": document.examSubjectForm["examSubject.code"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "代码已存在！！！");
    [/@]

    [@b.textfield label="名称" name="examSubject.name" value=(examSubject.name)! required="true" maxlength="100" style="width:200px"/]
    [@b.datepicker id="beginOn" label="启用日期" name="examSubject.beginOn" value=(examSubject.beginOn?string('yyyy-MM-dd'))?default('') required="true" style="width:200px" format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'endOn\\')}"/]
    [@b.datepicker id="endOn" label="截止日期" name="examSubject.endOn" value=(examSubject.endOn?string('yyyy-MM-dd'))?default('') style="width:200px" format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'beginOn\\')}"/]
    [@b.formfoot]
      <input type="hidden" name="examSubject.id" value="${(examSubject.id)!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
[@b.foot/]
