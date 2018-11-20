[#ftl]
[#macro certSelects formName examSubjectElement certTypeElement certLevelElement divisionElement examTimeElement styleHTML]
  [#if (examSubjectElement.empty!"")?length == 0]
    [@b.select label="大类" name=examSubjectElement.name items=examSubjects?sort_by(["code"]) required=(examSubjectElement.required)! value=(examSubjectElement.value)! comment=(examSubjectElement.comment)! style=styleHTML!/]
  [#else]
    [@b.select label="大类" name=examSubjectElement.name items=examSubjects?sort_by(["code"]) empty=examSubjectElement.empty!"..." required=(examSubjectElement.required)! value=(examSubjectElement.value)! comment=(examSubjectElement.comment)! style=styleHTML!/]
  [/#if]
  [@b.select label="科目/子类" name=certTypeElement.name empty=(certTypeElement.empty)!"..." required=(certTypeElement.required)! value=(certTypeElement.value)! comment=(certTypeElement.comment)! style=styleHTML!/]
  [@b.select label="级别" name=certLevelElement.name empty=(certLevelElement.empty)!"..." required=(certLevelElement.required)! value=(certLevelElement.value)! comment=(certLevelElement.comment)! style=styleHTML!/]
  [@b.select label="省份" name=divisionElement.name empty=(divisionElement.empty)!"..." required=(divisionElement.required)! value=(divisionElement.value)! comment=(divisionElement.comment)! style=styleHTML!/]
  [@b.select label="报考时间" name=examTimeElement.name empty=(examTimeElement.empty)!"..." required=(examTimeElement.required)! value=(examTimeElement.value)! comment=(examTimeElement.comment)! style=styleHTML!/]
  <script>
    $(function() {
      $(document).ready(function() {
        var form = document.${formName};

        var examSubject = form["${examSubjectElement.name}"];
        var certType = form["${certTypeElement.name}"];
        var certLevel = form["${certLevelElement.name}"];
        var division = form["${divisionElement.name}"];
        var examTime = form["${examTimeElement.name}"];

        var certTypeId = certType.value;
        var certLevelId = certLevel.value;
        var divisionId = division.value;
        var examTimeId = examTime.value;

        $(examSubject).change(function() {
          $(certType).empty();
          $(certLevel).empty();
          $(division).empty();
          $(examTime).empty();

          $.ajax({
            "type": "POST",
            "url": "${b.url("!dataAjax")}",
            "async": false,
            "dataType": "json",
            "data": {
              "from": "examSubject",
              "examSubjectId": $(this).val()
            },
            "success": function(data) {
              $(certType).append(data.certTypeOptions);
              $(certLevel).append(data.certLevelOptions);
              $(division).append(data.divisionOptions);
              $(examTime).append(data.examTimeOptions);

              if (certTypeId) {
                $(certType).val(certTypeId);

                certTypeId = null;
              }

              if (certLevelId) {
                $(certLevel).change();
                $(certLevel).val(certLevelId);

                certLevelId = null;
                return;
              }
              if (divisionId) {
                $(division).change();
                $(division).val(divisionId);

                divisionId = null;
                return;
              }
              if (examTimeId) {
                $(examTime).val(examTimeId);
                examTimeId = null;
              }
            }
          });
        });

        $(certType).change(function() {
          $(certLevel).empty();
          $(division).empty();
          $(examTime).empty();

          $.ajax({
            "type": "POST",
            "url": "${b.url("!dataAjax")}",
            "async": false,
            "dataType": "json",
            "data": {
              "from": "certType",
              "examSubjectId": $(examSubject).val(),
              "certTypeId": $(this).val()
            },
            "success": function(data) {
              $(certLevel).append(data.certLevelOptions);
              $(division).append(data.divisionOptions);
              $(examTime).append(data.examTimeOptions);

              $(certLevel).val(certLevelId);

              certLevelId = null;

              if (divisionId) {
                $(division).change();
                $(division).val(divisionId);

                divisionId = null;
                return;
              }
              if (examTimeId) {
                $(examTime).val(examTimeId);
                examTimeId = null;
              }
            }
          });
        });

        $(certLevel).change(function() {
          $(division).empty();
          $(examTime).empty();

          $.ajax({
            "type": "POST",
            "url": "${b.url("!dataAjax")}",
            "async": false,
            "dataType": "json",
            "data": {
              "from": "certLevel",
              "examSubjectId": $(examSubject).val(),
              "certTypeId": $(certType).val(),
              "certLevelId": $(this).val()
            },
            "success": function(data) {
              $(division).append(data.divisionOptions);
              $(examTime).append(data.examTimeOptions);

              $(division).val(divisionId);

              divisionId = null;

              if (examTimeId) {
                $(examTime).val(examTimeId);
                examTimeId = null;
              }
            }
          });
        });

        $(division).change(function() {
          $(examTime).empty();

          console.log(examTimeId);

          $.ajax({
            "type": "POST",
            "url": "${b.url("!dataAjax")}",
            "async": false,
            "dataType": "html",
            "data": {
              "from": "division",
              "examSubjectId": $(examSubject).val(),
              "certTypeId": $(certType).val(),
              "certLevelId": $(certLevel).val(),
              "divisionId": $(this).val()
            },
            "success": function(data) {
              $(examTime).append(data);
            }
          });
        });

        $(examSubject).change();
      });
    });
  </script>
[/#macro]
