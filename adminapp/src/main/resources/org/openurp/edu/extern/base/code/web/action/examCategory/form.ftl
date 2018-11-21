[#ftl]
[@b.head/]
  [@b.toolbar title="校外考试类型配置"]
    bar.addBack();
  [/@]
  [@b.form name="examCategoryForm" action="!save" target="examCategories" theme="list"]
    [#assign elementSTYLE = "width: 200px"/]
    [@b.textfield label="代码" name="examCategory.code" value=(examCategory.code)! required="true" maxlength="30" style=elementSTYLE/]
    [@b.validity]
      $("[name='examCategory.code']", document.examCategoryForm).require().assert(function() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": document.examCategoryForm["examCategory.id"].value,
            "code": document.examCategoryForm["examCategory.code"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "代码已存在！！！");
    [/@]
    [@b.textfield label="名称" name="examCategory.name" value=(examCategory.name)! required="true" maxlength="100" style=elementSTYLE/]
    [@b.textfield label="英文名称" name="examCategory.enName" value=(examCategory.enName)! maxlength="100" style=elementSTYLE/]
    [@b.datepicker id="beginOn" label="生效时间" name="examCategory.beginOn" value=(examCategory.beginOn?string('yyyy-MM-dd'))?default('') required="true" style=elementSTYLE format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'endOn\\')}"/]
    [@b.datepicker id="endOn" label="失效时间" name="examCategory.endOn" value=(examCategory.endOn?string('yyyy-MM-dd'))?default('') style=elementSTYLE format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'beginOn\\')}"/]
    [@b.formfoot]
      <input type="hidden" name="examCategory.id" value="${(examCategory.id)!}"/>
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
