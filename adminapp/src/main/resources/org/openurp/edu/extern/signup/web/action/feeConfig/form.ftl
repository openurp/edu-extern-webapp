[#ftl]
[@b.head/]
[@b.form name="examFeeConfigForm" target="main" action="!save" theme="list" title="缴费设置维护"]
    [@b.select label="收费项目" required="true" items=feeTypes name="examFeeConfig.feeType.id" value=(examFeeConfig.feeType.id)! empty="..."/]
    [@b.select label="开放状态" items={'1':'开放','0':'关闭'} required="true" name="examFeeConfig.opened" value=(examFeeConfig.opened?string("1","0"))!/]
    [@b.startend label="开放时间范围" name="examFeeConfig.openAt,examFeeConfig.closeAt" start=(examFeeConfig.openAt)! end=(examFeeConfig.closeAt)! format="yyyy-MM-dd HH:mm" readonly="readonly"/]
    [@b.formfoot]
        <input type="hidden" value="${(examFeeConfig.project.id)!(project.id)}" name="examFeeConfig.project.id"/>
        <input type="hidden" value="${(examFeeConfig.semester.id)!(semester.id)}" name="examFeeConfig.semester.id"/>
        <input type="hidden" value="${(examFeeConfig.id)!}" name="examFeeConfig.id"/>
        [@b.submit value="action.submit"/]&nbsp;
        <input type="reset"  name="reset1" value="${b.text("action.reset")}" class="buttonStyle" />
    [/@]
[/@]
<script>
    jQuery(function(){
        document.examFeeConfigForm["examFeeConfig.openAt"].onfocus = function(){
            WdatePicker({
                dateFmt:'yyyy-MM-dd HH:mm',
                maxDate:'#F{$dp.$D(\''+ document.examFeeConfigForm["examFeeConfig.closeAt"].id+'\')}'
            });
        };
        document.examFeeConfigForm["examFeeConfig.closeAt"].onfocus = function(){
            WdatePicker({
                dateFmt:'yyyy-MM-dd HH:mm',
                minDate:'#F{$dp.$D(\''+ document.examFeeConfigForm["examFeeConfig.openAt"].id +'\')}'
            });        
        };
    });
</script>
[@b.foot/]
