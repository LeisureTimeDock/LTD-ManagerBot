package top.r3944realms.ltdmanager.core.init

import top.r3944realms.ltdmanager.module.BaseModule

object ModuleRegistry {
   private val registry: MutableMap<String, BaseModule> = mutableMapOf()

   fun register(baseModule: BaseModule) {
      registry.putIfAbsent(baseModule.name, baseModule)
   }

   fun get(moduleName: String): BaseModule? {
      return registry[moduleName]
   }
}