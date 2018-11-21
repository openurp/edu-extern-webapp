[#ftl]
[@b.head/]
  [@b.toolbar title="校外考试类型配置"]
    bar.addBack();
  [/@]
  [@b.form name="examSubjectForm" action="!save" target="examSubjects" theme="list"]
    [#assign elementSTYLE = "width: 200px"/]
    [@b.textfield label="代码" name="examSubject.code" value=(examSubject.code)! required="true" maxlength="30" style=elementSTYLE/]
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
    [@b.textfield label="名称" name="examSubject.name" value=(examSubject.name)! required="true" maxlength="100" style=elementSTYLE/]
    [@b.textfield label="英文名称" name="examSubject.enName" value=(examSubject.enName)! maxlength="100" style=elementSTYLE/]
    [@b.select label="考试类型" name="examSubject.category.id" items=examCategories?sort_by("name") empty="..." required="true" value=(examSubject.category.id)! style=elementSTYLE/]
    [@b.datepicker id="beginOn" label="生效时间" name="examSubject.beginOn" value=(examSubject.beginOn?string('yyyy-MM-dd'))?default('') required="true" style=elementSTYLE format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'endOn\\')}"/]
    [@b.datepicker id="endOn" label="失效时间" name="examSubject.endOn" value=(examSubject.endOn?string('yyyy-MM-dd'))?default('') style=elementSTYLE format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'beginOn\\')}"/]
    [@b.formfoot]
      <input type="hidden" name="examSubject.id" value="${(examSubject.id)!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
  <script>
    $(function() {
      $(document).ready(function() {
        $(".Wdate").attr("readOnly", "");
      });
    });
  </script>
[@b.foot/]
