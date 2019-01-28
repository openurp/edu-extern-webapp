[#ftl]
[@b.head/]
[@b.toolbar title="校外报考统计"/]
<div>
<table class="indexpanel">
<tr valign="top">
    <td  class="index_view">
            [@b.form target="examSignupStatIndex" title="ui.searchForm" name="examSignupStatForm"]
            <input type="hidden" name="major.enabled" value="1">
            <input type="hidden" name="orderBy" value="">
                        <table class="search-widget">
                             <tr>
                                  <td  class="infoTitle" align="left" valign="bottom">
                                   <img src="${b.static_url("bui","icons/16x16/actions/info.png")} align="top"/>
                                      <B>统计项目</B>
                                  </td>
                              </tr>
                               <tr>
                                  <td  colspan="8" style="font-size:0px">
                                      <img src="${b.static_url("bui","icons/16x16/actions/keyline.png")}" height="2" width="100%" align="top">
                                  </td>
                               </tr>

                                <tr>
                                 <td class="padding" id="defaultItem" onclick="signup()" onmouseover="MouseOver(event)" onmouseout="MouseOut(event)" title="点击按报名进行统计">
                                 &nbsp;&nbsp;<image src="${b.static_url("bui","icons/16x16/actions/list.png")}" align="bottom" >报名统计
                                 </td>
                             </tr>
                           <tr>
                                 <td class="padding"  onclick="statGrade()"  onmouseover="MouseOver(event)" onmouseout="MouseOut(event)" title="点击按成绩进行统计">
                                 &nbsp;&nbsp;<image src="${b.static_url("bui","icons/16x16/actions/list.png")}">成绩统计
                                 </td>
                           </tr>
                           <tr>
                                 <td class="padding"  onclick="passRate()"  onmouseover="MouseOver(event)" onmouseout="MouseOut(event)" title="点击按成绩进行统计">
                                   &nbsp;&nbsp;<image src="${b.static_url("bui","icons/16x16/actions/list.png")}">及格率统计
                                  </td>
                           </tr>
                           <tr>
                               <td valign="top">
                                     <iframe name="statFrame" src="#" width="100%" frameborder="0" scrolling="no"></iframe>
                                </td>
                          </tr>
                        </table>
            [/@]
    </td>
    <td class="index_content">
        [@b.div id="examSignupStatIndex" href="!signupsearch" /]
    </td>
</tr>
</table>
</div>
<script>
    var form = document.examSignupStatForm;
           function signup(){
                   form.action = "examSignupStat.action?method=signupsearch"
                   bg.form.submit(form);
           }
              function statGrade(){
                   form.action = "examSignupStat.action?method=statGradeSearch"
                   bg.form.submit(form);
           }
              function passRate(){
                   form.action = "examSignupStat.action?method=passRateSearch"
                   bg.form.submit(form);
           }

           function MouseOver(e){
            var o=bg.event.getTarget(e);
            while (o&&o.tagName.toLowerCase()!="td"){o=o.parentNode;}
            if(o)o.className="toolbar-item-transfer";
        }
        /**
         * 当鼠标离开工具栏的按钮时
         */
        function MouseOut(e){
            var o=bg.event.getTarget(e);
            while (o&&o.tagName.toLowerCase()!="td"){o=o.parentNode;}
            if(o)o.className="toolbar-item";
            }
</script>
[@b.foot/]
