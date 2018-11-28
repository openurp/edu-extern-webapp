[#ftl]
[@b.head/]
  [@b.toolbar title="校外科目大类配置"]
    bar.addBack();
  [/@]
  [@b.form name="externSchoolForm" action="!save" target="externSchools" theme="list"]
    [#assign elementSTYLE = "width: 200px"/]
    [@b.textfield label="代码" name="externSchool.code" value=(externSchool.code)! required="true" maxlength="30" style=elementSTYLE/]
    [@b.validity]
      $("[name='externSchool.code']", document.externSchoolForm).require().assert(function() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": document.externSchoolForm["externSchool.id"].value,
            "code": document.externSchoolForm["externSchool.code"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "代码已存在！！！");
    [/@]
    [@b.textfield label="名称" name="externSchool.name" value=(externSchool.name)! required="true" maxlength="100" style=elementSTYLE/]
    [@b.textfield label="英文名称" name="externSchool.enName" value=(externSchool.enName)! maxlength="100" style=elementSTYLE/]
    [@b.select label="国家地区" name="externSchool.country.id" items=countries empty="..." value=(externSchool.country.id)! style=elementSTYLE/]
    [@b.datepicker id="beginOn" label="生效时间" name="externSchool.beginOn" value=(externSchool.beginOn?string('yyyy-MM-dd'))?default('') required="true" style=elementSTYLE format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'endOn\\')}"/]
    [@b.datepicker id="endOn" label="失效时间" name="externSchool.endOn" value=(externSchool.endOn?string('yyyy-MM-dd'))?default('') style=elementSTYLE format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'beginOn\\')}"/]
    [@b.formfoot]
      <input type="hidden" name="externSchool.id" value="${(externSchool.id)!}"/>
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
