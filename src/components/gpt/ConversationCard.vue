<template>

  <div class="person-card" :class="{ activeCard: conversationInfo.conversationId === pcCurrent }">
    <div class="info">
      <el-icon  :size="16" style="margin: auto 5px auto 10px;" class="icon">
        <ChatLineSquare/>
      </el-icon>
      <div class="info-detail" >
        <div class="name">
          {{ conversationInfo.firstMessage }}
        </div>
      </div>
      <el-popconfirm title="Are you sure to delete this?" @confirm="deleteConversation(conversationInfo.conversationId)">
        <template #reference>
          <el-icon  :size="16" style="margin: auto 5px auto 10px;" class="icon">
            <Delete/>
          </el-icon>
        </template>
      </el-popconfirm>
    </div>
  </div>

</template>

<script setup>
import {Delete} from "@element-plus/icons-vue";


const emit = defineEmits(['deleteConversation'])
const deleteConversation = (id) => {
  // 传递给父组件
  emit('deleteConversation', id)
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps({
  conversationInfo: Object,
  pcCurrent: String
})

</script>

<style lang="scss" scoped>
.person-card {
  width: 200px;
  height: 60px;
  border-radius: 10px;
  border: 1px solid var(--md-ref-palette-neutral90);
  background-color: var(--md-ref-palette-neutral-variant100);
  position: relative;
  margin: 25px 0;
  cursor: pointer;
  .info {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 95%;
    transform: translate(-50%, -50%);
    color: var(--md-sys-color-on-surface-light);
    display: flex;
    .info-detail {
      margin-top: 5px;
      margin-left: 5px;
      width: 120px;

      .name {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin-bottom: 5px;
      }
    }
    .icon {
      width: 20px
    }
  }
  &:hover {
    background-color: var(--md-sys-color-primary-light);
    transition: 0.3s;
    border: none;
    box-shadow: 0px 0px 10px 0px var(--md-sys-color-primary-light);
    // box-shadow:  0 5px 20px rgba(251, 152, 11, .5);
    .info {
      color: #fff;
    }
  }
}
.activeCard {
    background-color: var(--md-sys-color-primary-light);
    transition: 0.3s;
    border: none;
    box-shadow: 3px 2px 10px 0px rgb(181, 187, 192);
    .info {
      color: var(--md-sys-color-on-primary-light);
    }
}
</style>
