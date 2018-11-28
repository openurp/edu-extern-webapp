[#ftl]
[@b.head/]
  [@b.toolbar title="教育类别配置"]
    bar.addBack();
  [/@]
  [@b.form name="eduCategoryForm" action="!save" target="eduCategories" theme="list"]
    [#assign elementSTYLE = "width: 200px"/]
    [@b.textfield label="代码" name="eduCategory.code" value=(eduCategory.code)! required="true" maxlength="30" style=elementSTYLE/]
    [@b.validity]
      $("[name='eduCategory.code']", document.eduCategoryForm).require().assert(function() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": document.eduCategoryForm["eduCategory.id"].value,
            "code": document.eduCategoryForm["eduCategory.code"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "代码已存在！！！");
    [/@]
    [@b.textfield label="名称" name="eduCategory.name" value=(eduCategory.name)! required="true" maxlength="100" style=elementSTYLE/]
    [@b.textfield label="英文名称" name="eduCategory.enName" value=(eduCategory.enName)! maxlength="100" style=elementSTYLE/]
    [@b.datepicker id="beginOn" label="生效时间" name="eduCategory.beginOn" value=(eduCategory.beginOn?string('yyyy-MM-dd'))?default('') required="true" style=elementSTYLE format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'endOn\\')}"/]
    [@b.datepicker id="endOn" label="失效时间" name="eduCategory.endOn" value=(eduCategory.endOn?string('yyyy-MM-dd'))?default('') style=elementSTYLE format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'beginOn\\')}"/]
    [@b.formfoot]
      <input type="hidden" name="eduCategory.id" value="${(eduCategory.id)!}"/>
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
