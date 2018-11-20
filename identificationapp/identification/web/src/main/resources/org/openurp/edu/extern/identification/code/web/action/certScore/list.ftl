[#ftl]
[@b.head/]
  [@b.grid items=certScores var="certScore"]
    [@b.gridbar]
      bar.addItem("${b.text("action.new")}", action.add());
      bar.addItem("${b.text("action.modify")}", action.edit());
      bar.addItem("${b.text("action.delete")}", action.remove("确认要删除吗？"));

      function info(id) {
        bg.form.addInput(action.getForm(), "certScore.id", id,"hidden");;
        bg.form.submit(action.getForm(), "${b.url("!info")}", null, null);
      }
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="大类" property="examSubject.name" width="120px"/]
      [@b.col title="科目/子类" property="certType.name" width="100px"]${(certScore.certType.name)!"全部/任何"}[/@]
      [@b.col title="证书级别" property="certLevel.name" width="60px"]${(certScore.certLevel.name)!"全部/任何"}[/@]
      [@b.col title="省份" property="division.name" width="60px"]${(certScore.division.code[0..1] + "-" + certScore.division.name)!"全国/任何"}[/@]
      [@b.col title="报考时间" property="examTime.name" width="60px"]${(certScore.examTime.name)!"全部/任何"}[/@]
      [@b.col title="可替课程"sortabled="false"]<a href="javascript:void(0)" onclick="info(${certScore.id})">[#list certScore.courses?sort_by(["id"]) as csCourse]${csCourse.course.name}(${csCourse.course.code})/${csCourse.score}/${csCourse.scoreValue!csCourse.score}[#if csCourse_has_next][#if csCourse_index gte 1]...[#break/][#else],[/#if][/#if][/#list]</a>[/@]
      [@b.col title="启动日期" property="beginOn" width="80px"]${certScore.beginOn?string("yyyy-MM-dd")}[/@]
      [@b.col title="截止日期" property="endOn" width="80px"]${(certScore.endOn?string("yyyy-MM-dd"))!"至今"}[/@]
    [/@]
  [/@]
[@b.foot/]
