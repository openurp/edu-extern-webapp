[#ftl]
[#macro certSelects formName styleHTML]
  [@b.field label="证书类型"]<span>${examSubject.name}</span><input type="hidden" name="examSubject.id" value="${examSubject.id}"/>[/@]
  [#list fields as field]
    [@b.select label=field.name name=field.innerField + ".id" required="true" style=styleHTML!/]
  [/#list]
  [#--FIXME 2018-04-10 zhouqi 将继续编写JavaScript--]
  [#--FIXME 2018-04-13 zhouqi 下边还是有问题--]
  <script>
    $(function() {
      $(document).ready(function() {
        var form = document.${formName};

        var examSubject = form["examSubject.id"];
        var examSubjectId = examSubject.value;
        [#list fields as field]
        var ${field.innerField} = form["${field.innerField}.id"];
        var ${field.innerField}Id = ${field.innerField}.value;
        [/#list]

        [#if fields?size gt 1]
          [#list fields as field]
            [#if field_index + 1 == fields?size]
              [#break/]
            [/#if]
        $(${field.innerField}).change(function() {
            [#if fields?size - field_index gt 1]
              [#list field_index + 1..fields?size - 1 as i]
          $(${fields[i].innerField}).empty();
              [/#list]
            [/#if]

          var dataParamMap = {};
          dataParamMap.from = "${field.innerField}";
          dataParamMap.empty = "请填写";
          dataParamMap.examSubjectId = examSubjectId;
            [#list 0..field_index as i]
          dataParamMap.${fields[i].innerField}Id = $(${fields[i].innerField}).val();
            [/#list]

          $.ajax({
            "type": "POST",
            "url": "${b.url("!dataAjax")}",
            "async": false,
            "dataType": "${(field.innerField == "division")?string("html", "json")}",
            "data": dataParamMap,
            "success": function(data) {
              [#list field_index + 1..fields?size - 1 as i]
              $(${fields[i].innerField}).append(data[#if field != "division"].${fields[i].innerField}Options[/#if]);
              [/#list]
            },
            "error": function(request, errMsg, e) {
              console.log(request);
              console.log(errMsg);
              console.log(errObj);
            }
          });
        });
          [/#list]
        [/#if]

        [#--TODO 2018-04-10 zhouqi 联动从这里开始--]
        (function() {
          var dataParamMap = {};
          dataParamMap.from = "examSubject";
          dataParamMap.empty = "请填写";
          dataParamMap.examSubjectId = examSubjectId;

          $.ajax({
            "type": "POST",
            "url": "${b.url("!dataAjax")}",
            "async": false,
            "dataType": "json",
            "data": dataParamMap,
            "success": function(data) {
              [#list fields as field]
              $(${field.innerField}).append(data.${field.innerField}Options);
              [/#list]
            }
          });
        })();
      });
    });
  </script>
[/#macro]
