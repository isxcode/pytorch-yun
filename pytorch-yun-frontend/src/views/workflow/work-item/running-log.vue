<!--
 * @Author: fanciNate
 * @Date: 2023-05-26 16:35:28
 * @LastEditTime: 2023-06-18 15:48:24
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /pytorch-yun/pytorch-yun-website/src/views/workflow/work-item/running-log.vue
-->
<template>
  <div
    id="content"
    class="running-log"
  >
    <pre
      v-if="logMsg"
      ref="preContentRef"
      @mousewheel="mousewheelEvent"
    >{{ logMsg }}</pre>
    <EmptyPage v-else />
  </div>
</template>

<script lang="ts" setup>
import { nextTick, onUnmounted, ref, defineExpose } from 'vue'
import EmptyPage from '@/components/empty-page/index.vue'
import { GetYarnLogData } from '@/services/schedule.service'

const logMsg = ref('')
const position = ref(true)
const timer = ref(null)
const preContentRef = ref(null)
const pubId = ref('')

function initData(id: string): void {
  pubId.value = id
  getLogData(pubId.value)
  if (!timer.value) {
    timer.value = setInterval(() => {
      getLogData(pubId.value)
    }, 3000)
  }
}

// 获取日志
function getLogData(id: string) {
  if (!id) {
    logMsg.value = ''
    return
  }
  GetYarnLogData({
    instanceId: id
  })
    .then((res: any) => {
      logMsg.value = res.data.yarnLog
      if (position.value) {
        nextTick(() => {
          scrollToButtom()
        })
      }
    })
    .catch(() => {
      logMsg.value = ''
      if (timer.value) {
          clearInterval(timer.value)
      }
      timer.value = null
    })
}

function scrollToButtom() {
  if (preContentRef.value) {
    document.getElementById('content').scrollTop = preContentRef.value?.scrollHeight // 滚动高度
  }
}

function mousewheelEvent(e: any) {
  if (!(e.deltaY > 0)) {
    position.value = false
  }
}

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
  timer.value = null
})

defineExpose({
  initData
})
</script>

<style lang="scss">
.running-log {
  pre {
    color: getCssVar('text-color', 'primary');
    font-size: 12px;
    line-height: 21px;
    margin: 0;
  }
  .empty-page {
    height: 50%;
  }
}
</style>
