import * as React from "react"
import {cn} from "@/lib/utils"

function Input({className, type, ...props}) {
    return (
        <input type={type}
               data-slot="input"
               className={cn("file:text-foreground placeholder:text-muted-foreground selection:bg-primary selection:text-primary-foreground border-input h-9 w-full min-w-0 rounded-md border bg-transparent px-3 py-1 text-base outline-none file:inline-flex file:h-7 file:border-0 file:bg-transparent file:text-sm file:font-medium disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50 md:text-sm",
                   "aria-invalid:ring-destructive/20 aria-invalid:border-destructive", "transition-colors focus-visible:border-ring focus-visible:bg-background",
                   className)}
               {...props}/>
    )
}

export {Input}
