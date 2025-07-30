export interface HeaderMessageProps {
    msg: {}
}

export const HeaderMessage = ({ msg }) => {
    return (<div
        className={`mb-6 p-4 rounded-lg ${msg.type === "error"
            ? "bg-red-200 text-red-800 border border-red-400"
            : "bg-green-200 text-green-800 border border-green-400"
            }`}
    >
        {msg.text}
    </div>)
}